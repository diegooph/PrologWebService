package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simmetrics.metrics.StringMetrics;

import java.sql.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoDaoImpl extends DatabaseConnection implements OrdemServicoDao {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void processaChecklistRealizado(@NotNull final Connection conn,
                                           @NotNull final Long codChecklistInserido,
                                           @NotNull final ChecklistInsercao checklist) throws Throwable {
        final Map<Long, List<InfosAlternativaAberturaOrdemServico>> infosAberturaMap =
                createAlternativasAberturaOrdemServico(
                        conn,
                        checklist.getCodModelo(),
                        checklist.getCodVersaoModeloChecklist(),
                        checklist.getPlacaVeiculo());
        final TipoOutrosSimilarityFinder similarityFinder = new TipoOutrosSimilarityFinder(StringMetrics.jaro());

        new OrdemServicoProcessor(
                codChecklistInserido,
                checklist,
                infosAberturaMap,
                similarityFinder).process(conn);
    }

    @NotNull
    @Override
    public List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                              @Nullable final Long codTipoVeiculo,
                                                              @Nullable final String placa,
                                                              @Nullable final StatusOrdemServico statusOrdemServico,
                                                              final int limit,
                                                              final int offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OS_GET_OS_LISTAGEM(?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, codUnidade);
            bindValueOrNull(stmt, 2, codTipoVeiculo, SqlType.BIGINT);
            bindValueOrNull(stmt, 3, placa, SqlType.TEXT);
            if (statusOrdemServico != null) {
                stmt.setString(4, statusOrdemServico.asString());
            } else {
                stmt.setNull(4, SqlType.TEXT.asIntTypeJava());
            }
            stmt.setInt(5, limit);
            stmt.setInt(6, offset);
            rSet = stmt.executeQuery();
            final List<OrdemServicoListagem> ordens = new ArrayList<>();
            while (rSet.next()) {
                ordens.add(OrdemServicoConverter.createOrdemServicoListagem(rSet));
            }
            return ordens;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<QtdItensPlacaListagem> getQtdItensPlacaListagem(
            @NotNull final Long codUnidade,
            @Nullable final Long codTipoVeiculo,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens,
            final int limit,
            final int offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_OS_GET_QTD_ITENS_PLACA_LISTAGEM(?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, codUnidade);
            bindValueOrNull(stmt, 2, codTipoVeiculo, SqlType.BIGINT);
            bindValueOrNull(stmt, 3, placaVeiculo, SqlType.TEXT);
            if (statusItens != null) {
                stmt.setString(4, statusItens.asString());
            } else {
                stmt.setNull(4, SqlType.TEXT.asIntTypeJava());
            }
            stmt.setInt(5, limit);
            stmt.setInt(6, offset);
            rSet = stmt.executeQuery();
            final List<QtdItensPlacaListagem> ordens = new ArrayList<>();
            while (rSet.next()) {
                ordens.add(OrdemServicoConverter.createQtdItensPlacaListagem(rSet));
            }
            return ordens;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(
            @NotNull final Long codUnidade,
            @NotNull final Long codOrdemServico) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OS_GET_ORDEM_SERVICO_RESOLUCAO(?, ?, ?)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codOrdemServico);
            stmt.setObject(3, Now.offsetDateTimeUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return OrdemServicoConverter.createHolderResolucaoOrdemServico(rSet);
            } else {
                throw new IllegalStateException("Erro ao buscar resolução de ordem de serviço");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @Nullable final PrioridadeAlternativa prioridade,
            @Nullable final StatusItemOrdemServico statusItens,
            final int limit,
            final int offset) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(?, ?, ?, ?, ?, ?, ?, ?)");
            // Código da unidade.
            stmt.setNull(1, SqlType.BIGINT.asIntTypeJava());
            // Código da Ordem de Serviço.
            stmt.setNull(2, SqlType.BIGINT.asIntTypeJava());
            stmt.setString(3, placaVeiculo);
            if (prioridade != null) {
                stmt.setString(4, prioridade.asString());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if (statusItens != null) {
                stmt.setString(5, statusItens.asString());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            stmt.setObject(6, Now.offsetDateTimeUtc());
            bindValueOrNull(stmt, 7, limit, SqlType.INTEGER);
            bindValueOrNull(stmt, 8, offset, SqlType.INTEGER);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return OrdemServicoConverter.createHolderResolucaoItensOrdemServico(rSet);
            } else {
                throw new IllegalStateException("Erro ao buscar resolução de itens de ordem de serviço");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(?, ?, ?, ?, ?, ?, ?, ?)");
            bindValueOrNull(stmt, 1, codUnidade, SqlType.BIGINT);
            bindValueOrNull(stmt, 2, codOrdemServico, SqlType.BIGINT);
            bindValueOrNull(stmt, 3, placaVeiculo, SqlType.TEXT);
            stmt.setNull(4, SqlType.TEXT.asIntTypeJava());
            bindValueOrNull(stmt, 5, statusItens != null ? statusItens.asString() : null, SqlType.VARCHAR);
            stmt.setObject(6, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setNull(7, SqlType.INTEGER.asIntTypeJava());
            stmt.setNull(8, SqlType.INTEGER.asIntTypeJava());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return OrdemServicoConverter.createHolderResolucaoItensOrdemServico(rSet);
            } else {
                throw new IllegalStateException("Erro ao buscar resolução de múltiplos itens de ordem de serviço");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO_ITENS SET " +
                    "  CPF_MECANICO = ?, " +
                    "  TEMPO_REALIZACAO = ?, " +
                    "  KM = ?, " +
                    "  STATUS_RESOLUCAO = ?, " +
                    "  DATA_HORA_CONSERTO = ?, " +
                    "  DATA_HORA_INICIO_RESOLUCAO = ?, " +
                    "  DATA_HORA_FIM_RESOLUCAO = ?, " +
                    "  FEEDBACK_CONSERTO = ? " +
                    "WHERE COD_UNIDADE = ? " +
                    "      AND CODIGO = ? " +
                    "      AND DATA_HORA_CONSERTO IS NULL;");
            final Long cpf = item.getCpfColaboradoResolucao();
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(item.getCodUnidadeOrdemServico(), conn);
            stmt.setLong(1, cpf);
            stmt.setLong(2, item.getDuracaoResolucaoMillis());
            stmt.setLong(3, item.getKmColetadoVeiculo());
            stmt.setString(4, StatusItemOrdemServico.RESOLVIDO.asString());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setObject(6, item.getDataHoraInicioResolucao().atZone(zoneId).toOffsetDateTime());
            stmt.setObject(7, item.getDataHoraFimResolucao().atZone(zoneId).toOffsetDateTime());
            stmt.setString(8, item.getFeedbackResolucao().trim());
            stmt.setLong(9, item.getCodUnidadeOrdemServico());
            stmt.setLong(10, item.getCodItemResolvido());
            if (stmt.executeUpdate() > 0) {
                updateStatusOs(conn, item.getCodOrdemServico(), item.getCodUnidadeOrdemServico());
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(item.getPlacaVeiculo(), item.getKmColetadoVeiculo(), conn);
                conn.commit();
            } else {
                throw new SQLException("Erro ao resolver o item");
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt, null);
        }
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO_ITENS SET " +
                    "  CPF_MECANICO = ?, " +
                    "  TEMPO_REALIZACAO = ?, " +
                    "  KM = ?, " +
                    "  STATUS_RESOLUCAO = ?, " +
                    "  DATA_HORA_CONSERTO = ?, " +
                    "  DATA_HORA_INICIO_RESOLUCAO = ?, " +
                    "  DATA_HORA_FIM_RESOLUCAO = ?, " +
                    "  FEEDBACK_CONSERTO = ? " +
                    "WHERE COD_UNIDADE = ? AND CODIGO = ANY (?) AND DATA_HORA_CONSERTO IS NULL;");
            final OffsetDateTime now = Now.offsetDateTimeUtc();
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(itensResolucao.getCodUnidadeOrdemServico(), conn);
            stmt.setLong(1, itensResolucao.getCpfColaboradorResolucao());
            stmt.setLong(2, itensResolucao.getDuracaoResolucaoMillis());
            stmt.setLong(3, itensResolucao.getKmColetadoVeiculo());
            stmt.setString(4, StatusItemOrdemServico.RESOLVIDO.asString());
            stmt.setObject(5, now);
            stmt.setObject(6, itensResolucao.getDataHoraInicioResolucao().atZone(zoneId).toOffsetDateTime());
            stmt.setObject(7, itensResolucao.getDataHoraFimResolucao().atZone(zoneId).toOffsetDateTime());
            stmt.setString(8, itensResolucao.getFeedbackResolucao().trim());
            stmt.setLong(9, itensResolucao.getCodUnidadeOrdemServico());
            stmt.setArray(10, PostgresUtils.listToArray(conn, SqlType.BIGINT, itensResolucao.getCodigosItens()));
            if (stmt.executeUpdate() == itensResolucao.getCodigosItens().size()) {
                fechaOrdensServicosComBaseItens(
                        conn,
                        itensResolucao.getCodUnidadeOrdemServico(),
                        itensResolucao.getCodigosItens(),
                        now);
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(
                        itensResolucao.getPlacaVeiculo(),
                        itensResolucao.getKmColetadoVeiculo(),
                        conn);
                conn.commit();
            } else {
                throw new IllegalStateException("Erro ao marcar os itens como resolvidos: "
                        + itensResolucao.getCodigosItens());
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void incrementaQtdApontamentos(
            @NotNull final Connection conn,
            @NotNull final List<Long> codItensOsIncrementaQtdApontamentos) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA " +
                    "SET QT_APONTAMENTOS = QT_APONTAMENTOS + 1 " +
                    "WHERE CODIGO = ANY(?);");
            stmt.setArray(
                    1,
                    PostgresUtils.listToArray(conn, SqlType.BIGINT, codItensOsIncrementaQtdApontamentos));
            if (stmt.executeUpdate() <= 0) {
                throw new SQLException("Não foi possível atualizar a quantidade de apontamentos dos ites:\n" +
                        "codItensOs: " + codItensOsIncrementaQtdApontamentos.toString());
            }
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private List<Long> getCodItensNok(@NotNull final List<PerguntaRespostaChecklist> listRespostas) {
        final List<Long> codItensNok = new ArrayList<>();
        for (final PerguntaRespostaChecklist resposta : listRespostas) {
            for (final AlternativaChecklist alternativa : resposta.getAlternativasResposta()) {
                if (alternativa.isSelected()) {
                    codItensNok.add(alternativa.getCodigo());
                }
            }
        }
        return codItensNok;
    }

    @NotNull
    private Map<Long, List<InfosAlternativaAberturaOrdemServico>> createAlternativasAberturaOrdemServico(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final String placaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_OS_ALTERNATIVAS_ABERTURA_OS(" +
                    "F_COD_MODELO_CHECKLIST        := ?, " +
                    "F_COD_VERSAO_MODELO_CHECKLIST := ?, " +
                    "F_PLACA_VEICULO               := ?)");
            stmt.setLong(1, codModelo);
            stmt.setLong(2, codVersaoModelo);
            stmt.setString(3, placaVeiculo);
            rSet = stmt.executeQuery();
            final Map<Long, List<InfosAlternativaAberturaOrdemServico>> map = new HashMap<>();
            while (rSet.next()) {
                // TODO: Alterar para usar código de contexto da alternativa.
                // TESTE: abrir um item de OS em uma versão do modelo, incrementar versão sem mudar alternativa que
                // abriu o item (manter contexto), realizar novo check apontando mesmo problema.
                // RESULTADO ESPERADO: deveria incrementar quantidade de apontamentos e não criar novo item.
                final Long codAlternativa = rSet.getLong("COD_ALTERNATIVA");
                List<InfosAlternativaAberturaOrdemServico> alternativas = map.get(codAlternativa);
                if (alternativas != null) {
                    alternativas.add(OrdemServicoConverter.createAlternativaChecklistAbreOrdemServico(rSet));
                } else {
                    alternativas = new ArrayList<>();
                    alternativas.add(OrdemServicoConverter.createAlternativaChecklistAbreOrdemServico(rSet));
                    map.put(codAlternativa, alternativas);
                }
            }
            return map;
        } finally {
            close(stmt, rSet);
        }
    }

    private void fechaOrdensServicosComBaseItens(@NotNull final Connection conn,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final List<Long> codigosItens,
                                                 @NotNull final OffsetDateTime now) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // Primeiro recuperamos os códigos de O.S. baseado nos códigos dos itens.
            stmt = conn.prepareStatement("SELECT DISTINCT COD_OS " +
                    "FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI " +
                    "WHERE COSI.CODIGO = ANY (?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codigosItens));
            rSet = stmt.executeQuery();
            final List<Long> codigosOrdensServicos = new ArrayList<>(codigosItens.size());
            if (rSet.next()) {
                do {
                    codigosOrdensServicos.add(rSet.getLong("COD_OS"));
                } while (rSet.next());
            } else {
                throw new IllegalStateException("Erro ao buscar os códigos das OSs para os itens: " + codigosItens);
            }

            // Depois podemos verificar se as Ordens de Serviços podem ser fechadas (caso não possuam mais itens
            // pendentes).
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO " +
                    "SET STATUS = ?, DATA_HORA_FECHAMENTO = ? " +
                    "WHERE COD_UNIDADE = ? " +
                    "      AND CODIGO = ? " +
                    "      AND NOT EXISTS((SELECT COSI.CODIGO " +
                    "           FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI " +
                    "           WHERE COSI.COD_UNIDADE = ? AND COSI.COD_OS = ? AND COSI.STATUS_RESOLUCAO = ?));");
            for (final Long codOs : codigosOrdensServicos) {
                stmt.setString(1, StatusOrdemServico.FECHADA.asString());
                stmt.setObject(2, now);
                stmt.setLong(3, codUnidade);
                stmt.setLong(4, codOs);
                stmt.setLong(5, codUnidade);
                stmt.setLong(6, codOs);
                stmt.setString(7, StatusItemOrdemServico.PENDENTE.asString());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } finally {
            close(stmt, rSet);
        }
    }

    private void updateStatusOs(@NotNull final Connection conn,
                                @NotNull final Long codOs,
                                @NotNull final Long codUnidadeOs) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO SET " +
                    "  STATUS = ?, " +
                    "  DATA_HORA_FECHAMENTO = ? " +
                    "WHERE COD_UNIDADE = ? " +
                    "      AND CODIGO = ? " +
                    "      AND (SELECT COUNT(*) FROM CHECKLIST_ORDEM_SERVICO_ITENS " +
                    "WHERE COD_UNIDADE = ? " +
                    "      AND COD_OS = ? " +
                    "      AND STATUS_RESOLUCAO = ?) = 0;");
            stmt.setString(1, StatusOrdemServico.FECHADA.asString());
            stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(3, codUnidadeOs);
            stmt.setLong(4, codOs);
            stmt.setLong(5, codUnidadeOs);
            stmt.setLong(6, codOs);
            stmt.setString(7, StatusItemOrdemServico.PENDENTE.asString());
            stmt.execute();
        } finally {
            close(stmt);
        }
    }
}