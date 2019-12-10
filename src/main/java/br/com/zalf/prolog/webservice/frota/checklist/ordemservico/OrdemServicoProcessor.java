package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 2019-11-13
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class OrdemServicoProcessor {
    @NotNull
    private final Long codChecklistInserido;
    @NotNull
    private final ChecklistInsercao checklist;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Map<Long, List<InfosAlternativaAberturaOrdemServico>> infosAberturaMap;
    @NotNull
    private final TipoOutrosSimilarityFinder similarityFinder;
    private PreparedStatement stmtUpdateQtdApontamentos;
    private PreparedStatement stmtCriacaoItens;
    private PreparedStatement stmtItensApontamentos;
    private Long codOrdemServico;

    OrdemServicoProcessor(@NotNull final Long codChecklistInserido,
                          @NotNull final ChecklistInsercao checklist,
                          @NotNull final Map<Long, List<InfosAlternativaAberturaOrdemServico>> infosAberturaMap,
                          @NotNull final TipoOutrosSimilarityFinder similarityFinder) {
        this.codChecklistInserido = codChecklistInserido;
        this.checklist = checklist;
        this.codUnidade = checklist.getCodUnidade();
        this.infosAberturaMap = infosAberturaMap;
        this.similarityFinder = similarityFinder;
    }

    void process(@NotNull final Connection conn) throws Throwable {
        try {
            stmtUpdateQtdApontamentos = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO_ITENS " +
                    "SET QT_APONTAMENTOS = ? WHERE CODIGO = ? AND STATUS_RESOLUCAO = ?;");
            stmtCriacaoItens = conn.prepareStatement("INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS" +
                            "(COD_UNIDADE, COD_OS, COD_PERGUNTA_PRIMEIRO_APONTAMENTO, COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO," +
                            " STATUS_RESOLUCAO, COD_CONTEXTO_PERGUNTA, COD_CONTEXTO_ALTERNATIVA) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO, COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO;",
                    // Para conseguirmos recuperar os retornos do insert através de batch.
                    Statement.RETURN_GENERATED_KEYS);
            stmtItensApontamentos = conn.prepareStatement("INSERT INTO CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS " +
                    " (COD_ITEM_ORDEM_SERVICO, COD_CHECKLIST_REALIZADO, COD_ALTERNATIVA, NOVA_QTD_APONTAMENTOS) " +
                    "VALUES (?, ?, ?, ?)");

            // Se uma nova O.S. tiver que ser aberta, conterá o código dela. Lembrando que um checklist pode abrir,
            // NO MÁXIMO, uma Ordem de Serviço.
            final List<ChecklistResposta> respostas = checklist.getRespostas();

            for (final ChecklistResposta resposta : respostas) {
                for (final ChecklistAlternativaResposta alternativaResposta : resposta.getAlternativasRespostas()) {
                    final List<InfosAlternativaAberturaOrdemServico> alternativas = infosAberturaMap
                            .get(alternativaResposta.getCodAlternativa());
                    // Comentar o pq do get(0)
                    if (alternativaResposta.isAlternativaSelecionada()
                            && alternativas.get(0).isDeveAbrirOrdemServico()) {
                        if (alternativas.get(0).jaTemItemPendente()) {
                            if (alternativaResposta.isTipoOutros()) {
                                final Optional<InfosAlternativaAberturaOrdemServico> bestMatch =
                                        similarityFinder.findBestMatch(alternativaResposta, alternativas);
                                if (bestMatch.isPresent()) {
                                    atualizaQtdApontamentosEInsereNovoApontamento(bestMatch.get());
                                } else {
                                    criaItemOrdemServico(conn, resposta, alternativaResposta, alternativas);
                                }
                            } else {
                                atualizaQtdApontamentosEInsereNovoApontamento(alternativas.get(0));
                            }
                        } else {
                            criaItemOrdemServico(conn, resposta, alternativaResposta, alternativas);
                        }
                    }
                }
            }

            StatementUtils.executeBatchAndValidate(
                    stmtUpdateQtdApontamentos,
                    1,
                    "Erro ao incrementar a quantidade de apontamentos");
            StatementUtils.executeBatchAndValidate(
                    stmtCriacaoItens,
                    1,
                    "Erro ao criar itens de O.S.");

            // Recupera informações dos itens inseridos via batch e cria o primeiro apontamento para cada um.
            // Note from the docs: the ResultSet object is automatically closed by the Statement object that generated
            // it when that Statement object is closed.
            final ResultSet itensInseridos = stmtCriacaoItens.getGeneratedKeys();
            while (itensInseridos.next()) {
                stmtItensApontamentos.setLong(1, itensInseridos.getLong(1));
                stmtItensApontamentos.setLong(2, codChecklistInserido);
                stmtItensApontamentos.setLong(3, itensInseridos.getLong(2));
                stmtItensApontamentos.setLong(4, 1);
                stmtItensApontamentos.addBatch();
            }

            StatementUtils.executeBatchAndValidate(
                    stmtItensApontamentos,
                    1,
                    "Erro ao inserir apontamento de item de O.S.");
        } finally {
            DatabaseConnection.close(stmtUpdateQtdApontamentos, stmtCriacaoItens, stmtItensApontamentos);
        }
    }

    private void atualizaQtdApontamentosEInsereNovoApontamento(
            @NotNull final InfosAlternativaAberturaOrdemServico infosAlternativaAbertura) throws SQLException {
        addBatchAtualizaQtdApontamentos(stmtUpdateQtdApontamentos, infosAlternativaAbertura);
        addBatchInsereNovoApontamento(
                stmtItensApontamentos,
                codChecklistInserido,
                infosAlternativaAbertura);
    }

    private void criaItemOrdemServico(@NotNull final Connection conn,
                                      @NotNull final ChecklistResposta resposta,
                                      @NotNull final ChecklistAlternativaResposta alternativaResposta,
                                      @NotNull final List<InfosAlternativaAberturaOrdemServico> alternativas) throws Throwable {
        if (codOrdemServico == null) {
            codOrdemServico = criarOrdemServico(conn, codUnidade, codChecklistInserido);
        }
        addBatchCriaItemOrdemServico(
                stmtCriacaoItens,
                codOrdemServico,
                codUnidade,
                resposta.getCodPergunta(),
                alternativaResposta.getCodAlternativa(),
                alternativas.get(0));
    }

    private void addBatchAtualizaQtdApontamentos(@NotNull final PreparedStatement stmtUpdateQtdApontamentos,
                                                 @NotNull final InfosAlternativaAberturaOrdemServico infoAberturaOrdemServico)
            throws SQLException {
        stmtUpdateQtdApontamentos.setLong(1, infoAberturaOrdemServico.getQtdApontamentosItem() + 1);
        stmtUpdateQtdApontamentos.setLong(2, infoAberturaOrdemServico.getCodItemOrdemServico());
        stmtUpdateQtdApontamentos.setString(3, StatusItemOrdemServico.PENDENTE.asString());
        stmtUpdateQtdApontamentos.addBatch();
    }

    private void addBatchInsereNovoApontamento(@NotNull final PreparedStatement stmtItensApontamentos,
                                               @NotNull final Long codChecklistInserido,
                                               @NotNull final InfosAlternativaAberturaOrdemServico infoAberturaOrdemServico)
            throws SQLException {
        stmtItensApontamentos.setLong(1, infoAberturaOrdemServico.getCodItemOrdemServico());
        stmtItensApontamentos.setLong(2, codChecklistInserido);
        stmtItensApontamentos.setLong(3, infoAberturaOrdemServico.getCodAlternativa());
        stmtItensApontamentos.setLong(4, infoAberturaOrdemServico.getQtdApontamentosItem() + 1);
        stmtItensApontamentos.addBatch();
    }

    private void addBatchCriaItemOrdemServico(@NotNull final PreparedStatement stmtCriacaoItens,
                                              @NotNull final Long codOrdemServico,
                                              @NotNull final Long codUnidade,
                                              @NotNull final Long codPergunta,
                                              @NotNull final Long codAlternativa,
                                              @NotNull final InfosAlternativaAberturaOrdemServico infoAberturaOrdemServico)
            throws SQLException {
        stmtCriacaoItens.setLong(1, codUnidade);
        stmtCriacaoItens.setLong(2, codOrdemServico);
        stmtCriacaoItens.setLong(3, codPergunta);
        stmtCriacaoItens.setLong(4, codAlternativa);
        stmtCriacaoItens.setString(5, StatusItemOrdemServico.PENDENTE.asString());
        stmtCriacaoItens.setLong(6, infoAberturaOrdemServico.getCodContextoPergunta());
        stmtCriacaoItens.setLong(7, infoAberturaOrdemServico.getCodContextoAlternativa());
        stmtCriacaoItens.addBatch();
    }

    @NotNull
    private Long criarOrdemServico(@NotNull final Connection conn,
                                   @NotNull final Long codUnidade,
                                   @NotNull final Long codChecklist) throws Throwable {
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO " +
                    "CHECKLIST_ORDEM_SERVICO(COD_UNIDADE, COD_CHECKLIST, STATUS) VALUES " +
                    "(?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codChecklist);
            stmt.setString(3, StatusOrdemServico.ABERTA.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao criar nova OS");
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }
}
