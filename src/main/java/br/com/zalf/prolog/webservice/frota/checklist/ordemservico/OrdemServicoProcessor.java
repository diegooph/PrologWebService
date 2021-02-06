package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 2019-11-13
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class OrdemServicoProcessor {
    private static final int EXECUTE_BATCH_SUCCESS = 0;
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
    @Nullable
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

    Long process(@NotNull final Connection conn) throws Throwable {
        try {
            stmtUpdateQtdApontamentos = conn.prepareCall(
                    "{CALL FUNC_CHECKLIST_OS_INCREMENTA_QTD_APONTAMENTOS_ITEM(" +
                            "F_COD_ITEM_ORDEM_SERVICO := ?, " +
                            "F_COD_CHECKLIST_REALIZADO := ?, " +
                            "F_COD_ALTERNATIVA := ?," +
                            "F_STATUS_RESOLUCAO := ?)}");
            stmtCriacaoItens = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_OS_INSERE_ITEM_OS(" +
                            "F_COD_UNIDADE := ?, " +
                            "F_COD_OS := ?, " +
                            "F_COD_PERGUNTA_PRIMEIRO_APONTAMENTO := ?, " +
                            "F_COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO := ?, " +
                            "F_STATUS_RESOLUCAO := ?, " +
                            "F_COD_CONTEXTO_PERGUNTA := ?, " +
                            "F_COD_CONTEXTO_ALTERNATIVA := ?, " +
                            "F_COD_CHECKLIST_REALIZADO := ?);");

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
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao incrementar a quantidade de apontamentos");
            StatementUtils.executeBatchAndValidate(
                    stmtCriacaoItens,
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao criar itens de O.S.");
        } finally {
            DatabaseConnection.close(stmtUpdateQtdApontamentos, stmtCriacaoItens);
        }
        return codOrdemServico;
    }

    private void atualizaQtdApontamentosEInsereNovoApontamento(
            @NotNull final InfosAlternativaAberturaOrdemServico infosAlternativaAbertura) throws SQLException {
        stmtUpdateQtdApontamentos.setLong(1, infosAlternativaAbertura.getCodItemOrdemServico());
        stmtUpdateQtdApontamentos.setLong(2, codChecklistInserido);
        stmtUpdateQtdApontamentos.setLong(3, infosAlternativaAbertura.getCodAlternativa());
        stmtUpdateQtdApontamentos.setString(4, StatusItemOrdemServico.PENDENTE.asString());
        stmtUpdateQtdApontamentos.addBatch();
    }

    private void criaItemOrdemServico(@NotNull final Connection conn,
                                      @NotNull final ChecklistResposta resposta,
                                      @NotNull final ChecklistAlternativaResposta alternativaResposta,
                                      @NotNull final List<InfosAlternativaAberturaOrdemServico> alternativas) throws Throwable {
        if (codOrdemServico == null) {
            codOrdemServico = criarOrdemServico(conn, codUnidade, codChecklistInserido);
        }

        stmtCriacaoItens.setLong(1, codUnidade);
        stmtCriacaoItens.setLong(2, codOrdemServico);
        stmtCriacaoItens.setLong(3, resposta.getCodPergunta());
        stmtCriacaoItens.setLong(4, alternativaResposta.getCodAlternativa());
        stmtCriacaoItens.setString(5, StatusItemOrdemServico.PENDENTE.asString());
        stmtCriacaoItens.setLong(6, alternativas.get(0).getCodContextoPergunta());
        stmtCriacaoItens.setLong(7, alternativas.get(0).getCodContextoAlternativa());
        stmtCriacaoItens.setLong(8, codChecklistInserido);
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
