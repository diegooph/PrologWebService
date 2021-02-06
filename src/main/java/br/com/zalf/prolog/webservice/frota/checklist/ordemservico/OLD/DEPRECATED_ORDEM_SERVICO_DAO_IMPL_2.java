package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created by jean on 10/08/16.
 */
@SuppressWarnings("Duplicates")
@Deprecated
public class DEPRECATED_ORDEM_SERVICO_DAO_IMPL_2 extends DatabaseConnection implements DEPRECATED_ORDEM_SERVICO_DAO_2 {
    /**
     * Busca os itens de uma ou mais OS, respeitando os parâmetros de filtro,
     * codigo da os / código da unidade / placa / status da OS
     */
    private static final String BUSCA_ITENS_OS = "SELECT * FROM ESTRATIFICACAO_OS E " +
            "WHERE  E.COD_OS::TEXT LIKE ? AND E.COD_UNIDADE::TEXT LIKE ? AND E.PLACA_VEICULO LIKE ? " +
            "AND E.STATUS_ITEM LIKE ? " +
            "ORDER BY E.PLACA_VEICULO, E.PRAZO ASC;";

    @Override
    public void consertaItem(@NotNull final ItemOrdemServico item) throws Throwable {
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
                    "  FEEDBACK_CONSERTO = ? " +
                    "WHERE COD_UNIDADE = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?) " +
                    "      AND COD_OS = ? " +
                    "      AND COD_PERGUNTA = ? " +
                    "      AND COD_ALTERNATIVA = ?;");
            final Long cpfMecanico = item.getMecanico().getCpf();
            stmt.setLong(1, cpfMecanico);
            stmt.setLong(2, item.getTempoRealizacaoConserto().toMillis());
            stmt.setLong(3, item.getKmVeiculoFechamento());
            stmt.setString(4, ItemOrdemServico.Status.RESOLVIDO.asString());
            stmt.setObject(5, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(6, item.getFeedbackResolucao().trim());
            stmt.setString(7, item.getPlaca());
            stmt.setLong(8, item.getCodOs());
            stmt.setLong(9, item.getPergunta().getCodigo());
            stmt.setLong(10, item.getPergunta().getAlternativasResposta().get(0).codigo);
            if (stmt.executeUpdate() > 0) {
                updateStatusOs(conn, item.getPlaca(), item.getCodOs());
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(item.getPlaca(), item.getKmVeiculoFechamento(), conn);
                conn.commit();
            } else {
                throw new SQLException("Erro ao consertar o item");
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public void consertaItens(@NotNull final ConsertoMultiplosItensOs itensConserto) throws Throwable {
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
                    "  FEEDBACK_CONSERTO = ? " +
                    "WHERE COD_UNIDADE = ? AND CODIGO = ANY (?) AND DATA_HORA_CONSERTO IS NULL;");
            final OffsetDateTime now = OffsetDateTime.now(Clock.systemUTC());
            stmt.setLong(1, itensConserto.getCpfColaboradorConserto());
            stmt.setLong(2, itensConserto.getDuracaoRealizacaoConserto().toMillis());
            stmt.setLong(3, itensConserto.getKmVeiculoConserto());
            stmt.setString(4, ItemOrdemServico.Status.RESOLVIDO.asString());
            stmt.setObject(5, now);
            stmt.setString(6, itensConserto.getFeedbackResolucao().trim());
            stmt.setLong(7, itensConserto.getCodUnidadeItensOs());
            stmt.setArray(8, PostgresUtils.listToArray(conn, SqlType.BIGINT, itensConserto.getCodigosItens()));
            if (stmt.executeUpdate() == itensConserto.getCodigosItens().size()) {
                fechaOrdensServicosComBaseItens(
                        conn,
                        itensConserto.getCodUnidadeItensOs(),
                        itensConserto.getCodigosItens(),
                        now);
                final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                veiculoDao.updateKmByPlaca(itensConserto.getPlacaVeiculo(), itensConserto.getKmVeiculoConserto(), conn);
                conn.commit();
            } else {
                throw new IllegalStateException("Erro ao marcar os itens como consertados: "
                        + itensConserto.getCodigosItens());
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            closeConnection(conn);
            closeStatement(stmt);
        }
    }

    @Override
    public List<OrdemServico> getOs(final String placa, final String status, final Long codUnidade,
                                    final String tipoVeiculo, final Integer limit, final Long offset) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<OrdemServico> oss = new ArrayList<>();
        try {
            conn = getConnection();
            /*
             * query que busca apenas os dados da OS, e não os itens
             */
            String query = "SELECT " +
                    "COS.CODIGO AS COD_OS, " +
                    "COS.COD_CHECKLIST, " +
                    "COS.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                    "COS.STATUS, " +
                    "C.PLACA_VEICULO, " +
                    "V.KM, " +
                    "C.DATA_HORA AT TIME ZONE ? AS DATA_HORA " +
                    "FROM CHECKLIST_ORDEM_SERVICO COS JOIN CHECKLIST C ON COS.COD_CHECKLIST = C.CODIGO " +
                    "AND C.COD_UNIDADE = COS.COD_UNIDADE " +
                    "JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO " +
                    "JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO " +
                    "WHERE C.PLACA_VEICULO LIKE ? AND COS.STATUS LIKE ? AND C.COD_UNIDADE = ? AND " +
                    "VT.CODIGO::TEXT LIKE ? " +
                    "ORDER BY COS.CODIGO DESC " +
                    "%s";
            if (limit != null && offset != null) {
                query = String.format(query, " LIMIT " + limit + "OFFSET " + offset);
            } else {
                query = String.format(query, "");
            }
            stmt = conn.prepareStatement(query);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, placa);
            stmt.setString(4, status);
            stmt.setLong(5, codUnidade);
            stmt.setString(6, tipoVeiculo);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final OrdemServico os = DeprecatedOrdemServicoConverter.createOrdemServicoSemItens(rSet);
                os.setItens(getItensOs(os.getVeiculo().getPlaca(), String.valueOf(os.getCodigo()), "%", conn, codUnidade));
                oss.add(os);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return oss;
    }

    @NotNull
    @Override
    public List<ItemOrdemServico> getItensOs(@NotNull final String placa,
                                             @NotNull final String statusItens,
                                             @NotNull final String prioridade,
                                             @Nullable final Integer limit,
                                             @Nullable final Long offset) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM ESTRATIFICACAO_OS E " +
                    "WHERE E.STATUS_ITEM LIKE ? AND E.PRIORIDADE LIKE ? AND E.PLACA_VEICULO = ? " +
                    "ORDER BY " +
                    "  E.PLACA_VEICULO, " +
                    "  CASE WHEN E.STATUS_ITEM = ? THEN 0 ELSE 1 END, " +
                    "  E.PRIORIDADE_ORDEM, " +
                    "  E.DATA_HORA ASC " +
                    "LIMIT ? OFFSET ?;");
            stmt.setString(1, statusItens);
            stmt.setString(2, prioridade);
            stmt.setString(3, placa);
            stmt.setString(4, ItemOrdemServico.Status.PENDENTE.asString());
            StatementUtils.bindValueOrNull(stmt, 5, limit, SqlType.INTEGER);
            StatementUtils.bindValueOrNull(stmt, 6, offset, SqlType.BIGINT);
            rSet = stmt.executeQuery();
            return DeprecatedOrdemServicoConverter.createItensOrdemServico(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ItemOrdemServico> getItensOs(@NotNull final Long codOs,
                                             @NotNull final Long codUnidade,
                                             @Nullable final String statusItemOs) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM ESTRATIFICACAO_OS EO " +
                    "WHERE EO.COD_OS = ? AND EO.COD_UNIDADE = ? AND EO.STATUS_ITEM LIKE ? " +
                    "ORDER BY EO.DATA_HORA_CONSERTO NULLS FIRST, EO.PRIORIDADE_ORDEM, EO.DATA_HORA DESC;");
            stmt.setLong(1, codOs);
            stmt.setLong(2, codUnidade);
            if (statusItemOs == null) {
                stmt.setString(3, "%");
            } else {
                // Caso seja um status, o parse para Enum é feito apenas para validar o atributo.
                stmt.setString(3, ItemOrdemServico.Status.fromString(statusItemOs).asString());
            }
            rSet = stmt.executeQuery();
            return DeprecatedOrdemServicoConverter.createItensOrdemServico(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public List<ItemOrdemServico> getItensOs(@NotNull final String placa,
                                             @NotNull final Date untilDate,
                                             @NotNull final ItemOrdemServico.Status statusItem,
                                             @NotNull final String prioridadeItem,
                                             final boolean itensCriticosRetroativos) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            String query = "SELECT * FROM ESTRATIFICACAO_OS E " +
                    "WHERE E.STATUS_ITEM = ? " +
                    "AND E.PRIORIDADE = ? " +
                    "AND E.PLACA_VEICULO = ? " +
                    "AND E.DATA_HORA::DATE %s (? AT TIME ZONE (SELECT TIMEZONE FROM func_get_time_zone_unidade(E.cod_unidade))) " +
                    "ORDER BY E.PLACA_VEICULO, E.PRIORIDADE_ORDEM, E.DATA_HORA DESC;";
            if (itensCriticosRetroativos) {
                query = String.format(query, "<=");
            } else {
                query = String.format(query, "=");
            }
            stmt = conn.prepareStatement(query);
            stmt.setString(1, statusItem.asString());
            stmt.setString(2, prioridadeItem);
            stmt.setString(3, placa);
            stmt.setDate(4, DateUtils.toSqlDate(untilDate));
            rSet = stmt.executeQuery();
            return DeprecatedOrdemServicoConverter.createItensOrdemServico(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void insertItemOs(final Checklist checklist, final Connection conn, final Long codUnidade) throws SQLException {
        Long tempCodOs = null;
        Long gerouOs = null;
        // Todas as os de uma unica placa.
        final List<OrdemServico> ordens = getOs(checklist.getPlacaVeiculo(), OrdemServico.Status.ABERTA.asString(), codUnidade, "%", null, null);
        for (final PerguntaRespostaChecklist pergunta : checklist.getListRespostas()) { //verifica cada pergunta do checklist
            for (final AlternativaChecklist alternativa : pergunta.getAlternativasResposta()) { // varre cada alternativa de uma pergunta
                if (alternativa.selected) {
                    if (ordens != null) {//verifica se ja tem algum item em aberto
                        tempCodOs = jaPossuiItemEmAberto(pergunta.getCodigo(), alternativa.codigo, ordens);
                        if (tempCodOs != null) {
                            Log.d("tempCodOs", tempCodOs.toString());
                        }
                    }
                    if (tempCodOs != null) {
                        incrementaQtApontamento(checklist.getPlacaVeiculo(), tempCodOs, pergunta.getCodigo(), alternativa.codigo, conn);
                    } else {
                        if (gerouOs != null) { //checklist ja gerou uma os -> deve inserir o item nessa os gerada
                            insertServicoOs(pergunta.getCodigo(), alternativa.codigo, gerouOs, checklist.getPlacaVeiculo(), conn);
                        } else {
                            gerouOs = createOs(checklist.getPlacaVeiculo(), checklist.getCodigo(), conn);
                            insertServicoOs(pergunta.getCodigo(), alternativa.codigo, gerouOs, checklist.getPlacaVeiculo(), conn);
                        }
                    }
                }
            }
        }
    }

    @NotNull
    @Override
    public List<ManutencaoHolder> getResumoManutencaoHolder(@NotNull final Long codUnidade,
                                                            @Nullable final Long codTipoVeiculo,
                                                            @Nullable final String placaVeiculo,
                                                            final boolean itensEmAberto,
                                                            final int limit,
                                                            final int offset) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<ManutencaoHolder> placas = new ArrayList<>();
        ManutencaoHolder holder;
        Veiculo v;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM PUBLIC.FUNC_CHECKLIST_GET_QTD_ITENS_OS(?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, codUnidade);
            bindValueOrNull(stmt, 2, codTipoVeiculo, SqlType.BIGINT);
            bindValueOrNull(stmt, 3, placaVeiculo, SqlType.TEXT);
            stmt.setBoolean(4, itensEmAberto);
            stmt.setInt(5, limit);
            stmt.setInt(6, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                holder = new ManutencaoHolder();
                v = new Veiculo();
                v.setPlaca(rSet.getString("PLACA_VEICULO"));
                v.setKmAtual(rSet.getLong("KM_ATUAL_VEICULO"));
                v.setAtivo(true);
                holder.setVeiculo(v);
                holder.setQtdCritica(rSet.getInt("ITENS_PRIORIDADE_CRITICA_ABERTOS"));
                holder.setQtdAlta(rSet.getInt("ITENS_PRIORIDADE_ALTA_ABERTOS"));
                holder.setQtdBaixa(rSet.getInt("ITENS_PRIORIDADE_BAIXA_ABERTOS"));
                placas.add(holder);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return placas;
    }

    /**
     * Cria uma ordem de serviço no banco de dados e retorna o código gerado na criação
     *
     * @param placa        uma placa
     * @param codChecklist código do checklist que originou a O.S.
     * @param conn         uma Connection
     * @return um Long com o códdigo da OS criada
     * @throws SQLException caso não seja possivel inserir a OS
     */
    private Long createOs(final String placa, final Long codChecklist, final Connection conn) throws SQLException {
        Log.d("criando OS", "Placa: " + placa + "checklist: " + codChecklist);
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico(CODIGO, cod_unidade, cod_checklist, status) VALUES\n" +
                    "((SELECT COALESCE(MAX(CODIGO), MAX(CODIGO), 0) + 1 AS CODIGO\n" +
                    "  FROM checklist_ordem_servico\n" +
                    "  WHERE cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)),\n" +
                    " (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?) RETURNING CODIGO");
            stmt.setString(1, placa);
            stmt.setString(2, placa);
            stmt.setLong(3, codChecklist);
            stmt.setString(4, OrdemServico.Status.ABERTA.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("codigo");
            } else {
                throw new SQLException("Erro ao criar nova OS");
            }
        } finally {
            closeConnection(null, stmt, rSet);
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
                stmt.setString(1, OrdemServico.Status.FECHADA.asString());
                stmt.setObject(2, now);
                stmt.setLong(3, codUnidade);
                stmt.setLong(4, codOs);
                stmt.setLong(5, codUnidade);
                stmt.setLong(6, codOs);
                stmt.setString(7, ItemOrdemServico.Status.PENDENTE.asString());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } finally {
            closeStatement(stmt);
            closeResultSet(rSet);
        }
    }

    private void insertServicoOs(final Long codPergunta, final Long codAlternativa, final Long codOs, final String placa, final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO checklist_ordem_servico_itens(COD_UNIDADE, COD_OS, cod_pergunta, cod_alternativa, status_resolucao)\n" +
                    "VALUES ((SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?),?,?,?,?)");
            stmt.setString(1, placa);
            stmt.setLong(2, codOs);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, codAlternativa);
            stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o serviço");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * Incrementa a quantidade de apontamentos de um item, caso ele ainda esteja em aberto e tenha sido
     * inserido em um novo checklist.
     *
     * @param placa          uma placa
     * @param codOs          um codigo
     * @param codPergunta    um codigo
     * @param codAlternativa um codigo
     * @param conn           uma Connection
     * @throws SQLException caso não seja possivel realizar a busca
     */
    private void incrementaQtApontamento(final String placa, final Long codOs, final Long codPergunta, final Long codAlternativa, final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE checklist_ordem_servico_itens SET qt_apontamentos =\n" +
                    "(SELECT qt_apontamentos FROM\n" +
                    "checklist_ordem_servico_itens WHERE\n" +
                    "cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)\n" +
                    "AND cod_os = ?\n" +
                    "AND cod_pergunta = ?\n" +
                    "AND cod_alternativa = ?\n" +
                    "AND status_resolucao = ? ) + 1\n" +
                    "WHERE cod_unidade = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?)\n" +
                    "AND cod_os = ?\n" +
                    "AND cod_pergunta = ?\n" +
                    "AND cod_alternativa = ?\n" +
                    "AND status_resolucao = ?");
            stmt.setString(1, placa);
            stmt.setLong(2, codOs);
            stmt.setLong(3, codPergunta);
            stmt.setLong(4, codAlternativa);
            stmt.setString(5, ItemOrdemServico.Status.PENDENTE.asString());
            stmt.setString(6, placa);
            stmt.setLong(7, codOs);
            stmt.setLong(8, codPergunta);
            stmt.setLong(9, codAlternativa);
            stmt.setString(10, ItemOrdemServico.Status.PENDENTE.asString());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao incrementar a quantidade de apontamentos");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * Busca os itens que compõe uma ou mais OS, usdao quando são buscadas as OS (e não o manutencaoHolder que busca só os itens)
     *
     * @param placa      Placa da OS, "%" para todas as placas
     * @param codOs      Código da OS, "%" para todas as OS
     * @param status     Status da OS, "%" para todos os status
     * @param conn       uma Connection
     * @param codUnidade Código da unidade
     * @return um list de ItemOrdemServico
     * @throws SQLException caso não seja possível realizar a busca
     */
    private List<ItemOrdemServico> getItensOs(final String placa, final String codOs, final String status, final Connection conn, final Long codUnidade) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(BUSCA_ITENS_OS);
            stmt.setString(1, String.valueOf(codOs));
            stmt.setString(2, String.valueOf(codUnidade));
            stmt.setString(3, placa);
            stmt.setString(4, status);
            rSet = stmt.executeQuery();
            return DeprecatedOrdemServicoConverter.createItensOrdemServico(rSet);
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    /**
     * Verifica se o item com problema já consta em alguma OS, caso já exista, retorna o código dessa OS
     *
     * @param codPergunta    um cod
     * @param codAlternativa um cod
     * @param oss            Todas as OS em aberto de uma placa
     * @return Long com o código da OS no qual o item se encontra em aberto
     */
    private Long jaPossuiItemEmAberto(final Long codPergunta, final long codAlternativa, final List<OrdemServico> oss) {
        Log.d("verificando se possui item em aberto", "Pergunta: " + codPergunta + "Alternativa: " + codAlternativa);
        for (final OrdemServico os : oss) {
            for (final ItemOrdemServico item : os.getItens()) {
                for (final Alternativa alternativa : item.getPergunta().getAlternativasResposta()) {
                    if (item.getPergunta().getCodigo().equals(codPergunta) && alternativa.codigo == codAlternativa && alternativa.tipo != Alternativa.TIPO_OUTROS
                            && item.getStatus().asString().equals(ItemOrdemServico.Status.PENDENTE.asString())) {
                        return os.getCodigo();
                    }
                }
            }
        }
        return null;
    }

    private void updateStatusOs(@NotNull final Connection conn,
                                @NotNull final String placa,
                                @NotNull final Long codOs) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE CHECKLIST_ORDEM_SERVICO SET " +
                    "  STATUS = ?, " +
                    "  DATA_HORA_FECHAMENTO = ? " +
                    "WHERE COD_UNIDADE = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?) " +
                    "      AND CODIGO = ? " +
                    "      AND (SELECT COUNT(*) FROM CHECKLIST_ORDEM_SERVICO_ITENS " +
                    "WHERE COD_UNIDADE = (SELECT COD_UNIDADE FROM VEICULO WHERE PLACA = ?) " +
                    "      AND COD_OS = ? " +
                    "      AND STATUS_RESOLUCAO = ?) = 0;");
            stmt.setString(1, OrdemServico.Status.FECHADA.asString());
            stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(3, placa);
            stmt.setLong(4, codOs);
            stmt.setString(5, placa);
            stmt.setLong(6, codOs);
            stmt.setString(7, ItemOrdemServico.Status.PENDENTE.asString());
            stmt.execute();
        } finally {
            closeStatement(stmt);
        }
    }

    private class CustomComparator implements Comparator<ManutencaoHolder> {

        /**
         * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates.
         */
        @Override
        public int compare(final ManutencaoHolder o1, final ManutencaoHolder o2) {
            final Integer valor1 = Double.compare(o1.getQtdCritica(), o2.getQtdCritica());
            if (valor1 != 0) {
                return valor1;
            }
            final Integer valor2 = Double.compare(o1.getQtdAlta(), o2.getQtdAlta());
            if (valor2 != 0) {
                return valor2;
            }
            return Double.compare(o1.getQtdBaixa(), o2.getQtdBaixa());
        }
    }
}