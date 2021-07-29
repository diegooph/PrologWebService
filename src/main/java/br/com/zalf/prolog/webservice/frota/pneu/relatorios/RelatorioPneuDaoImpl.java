package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoV2;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.QtdDiasAfericoesVencidas;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Classe responsável por estratificar os dados dos pneus.
 *
 * @author jean
 */
public class RelatorioPneuDaoImpl extends DatabaseConnection implements RelatorioPneuDao {
    private static final String TAG = RelatorioPneuDaoImpl.class.getSimpleName();

    public RelatorioPneuDaoImpl() {

    }

    @Override
    public void getFarolAfericaoCsv(@NotNull final OutputStream outputStream,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getFarolAfericaoStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getPneusComDesgasteIrregularCsv(@NotNull final OutputStream outputStream,
                                                @NotNull final List<Long> codUnidades,
                                                @Nullable final StatusPneu statusPneu) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPneusComDesgasteIrregularStmt(conn, codUnidades, statusPneu);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getPneusComDesgasteIrregularReport(@NotNull final List<Long> codUnidades,
                                                     @Nullable final StatusPneu statusPneu) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPneusComDesgasteIrregularStmt(conn, codUnidades, statusPneu);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getStatusAtualPneusCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getStatusAtualPneusStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getStatusAtualPneusReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getStatusAtualPneusStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getKmRodadoPorPneuPorVidaEmLinhasCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getKmRodadoPorPneuPorVidaEmLinhas(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getKmRodadoPorPneuPorVidaEmLinhasReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getKmRodadoPorPneuPorVidaEmLinhas(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getKmRodadoPorPneuPorVidaEmColunasCsv(@NotNull final OutputStream outputStream,
                                                      @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_pneu_relatorio_km_rodado_por_vida_base(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            // Não retornar nada não é erro, as unidades filtradas podem não ter pneus.
            if (rSet.next()) {
                final List<PneuKmRodadoPorVida> vidasPneu = new ArrayList<>();
                do {
                    vidasPneu.add(new PneuKmRodadoPorVida(
                            rSet.getString("unidade_alocado"),
                            rSet.getLong("cod_pneu"),
                            rSet.getString("cod_cliente_pneu"),
                            rSet.getString("dimensao"),
                            rSet.getString("marca"),
                            rSet.getString("modelo"),
                            rSet.getString("vida_pneu"),
                            rSet.getBigDecimal("valor_vida"),
                            rSet.getLong("km_rodado_vida"),
                            rSet.getString("valor_por_km_vida"),
                            rSet.getLong("km_rodado_todas_vidas")));
                } while (rSet.next());

                new CsvWriter
                        .Builder(outputStream)
                        .withCsvReport(new RelatorioKmRodadoPorVidaEmColuna(vidasPneu))
                        .build()
                        .write();
            } else {
                new CsvWriter
                        .Builder(outputStream)
                        .withCsvReport(new RelatorioKmRodadoPorVidaEmColuna(Collections.emptyList()))
                        .build()
                        .write();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getAfericoesAvulsasCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAfericoesAvulsasStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsasReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAfericoesAvulsasStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<Faixa> getQtdPneusByFaixaSulco(@NotNull final List<Long> codUnidades,
                                               @NotNull final List<String> status) throws SQLException {
        final List<Double> valores = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM func_relatorio_pneus_by_faixa_sulco(?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, status));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                valores.add(rSet.getDouble("ALTURA_SULCO_CENTRAL"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        if (valores.isEmpty()) {
            return new ArrayList<>();
        } else {
            return getFaixas(valores);
        }
    }

    @Override
    public void getPrevisaoTrocaEstratificadoCsv(@NotNull final OutputStream outputStream,
                                                 @NotNull final List<Long> codUnidades,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getPrevisaoTrocaEstratificadoReport(@NotNull final List<Long> codUnidades,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getPrevisaoTrocaConsolidadoCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final List<Long> codUnidades,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaConsolidadoStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getPrevisaoTrocaConsolidadoReport(@NotNull final List<Long> codUnidades,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPrevisaoTrocaConsolidadoStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getAderenciaPlacasCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final List<Long> codUnidades,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaPlacasStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getAderenciaPlacasReport(@NotNull final List<Long> codUnidades,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaPlacasStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getPneusDescartadosReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPneusDescartadosStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getPneusDescartadosCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPneusDescartadosStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getDadosUltimaAfericaoCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidades) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosUltimaAfericaoStatement(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getDadosUltimaAfericaoReport(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosUltimaAfericaoStatement(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getResumoGeralPneusCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @Nullable final String status) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoGeralPneusStatement(conn, codUnidades, status);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getResumoGeralPneusReport(@NotNull final List<Long> codUnidades,
                                            @Nullable final String status) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoGeralPneusStatement(conn, codUnidades, status);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @Deprecated
    public List<Aderencia> getAderenciaByUnidade(final int ano, final int mes, final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        final List<Aderencia> aderencias = new ArrayList<>();
        final AfericaoDaoV2 afericaoDao = Injection.provideAfericaoDao();
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final Restricao restricao = afericaoDao.getRestricaoByCodUnidade(codUnidade);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.MONTH, mes - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        final Date dataInicial = new Date(calendar.getTimeInMillis());

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final Date dataFinal = new Date(calendar.getTimeInMillis());

        final int ultimoDia;
        final int calendarYear = calendar.get(Calendar.YEAR);
        final int calendarMonth = calendar.get(Calendar.MONTH);
        /* verifica se o mes procurado é o mesmo mes corrente, se for, pega o dia atual, caso contrário
		 pega o ultimo dia do mês */
        if (calendarYear == ano && calendarMonth + 1 == mes) {
            ultimoDia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        } else {
            ultimoDia = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        try {
            conn = getConnection();
            final int totalVeiculos = veiculoDao.getTotalVeiculosByUnidade(codUnidade, conn);
            final double meta = totalVeiculos / restricao.getPeriodoDiasAfericaoPressao();
            stmt = conn.prepareStatement("SELECT EXTRACT(DAY from (A.DATA_HORA AT TIME ZONE ?)) AS DIA," +
                    " COUNT(EXTRACT(DAY from (A.DATA_HORA AT TIME ZONE ?))) AS REALIZADAS\n" +
                    "FROM AFERICAO A JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO\n" +
                    "WHERE A.DATA_HORA >= (? AT TIME ZONE ?) AND A.DATA_HORA <= (? AT TIME ZONE ?) AND\n" +
                    "      V.COD_UNIDADE = ?\n" +
                    "GROUP BY 1\n" +
                    "ORDER BY 1;");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setString(2, zoneId);
            stmt.setDate(3, dataInicial);
            stmt.setString(4, zoneId);
            stmt.setDate(5, dataFinal);
            stmt.setString(6, zoneId);
            stmt.setLong(7, codUnidade);
            rSet = stmt.executeQuery();
            int dia = 1;
            while (rSet.next()) {
                while (dia < rSet.getInt("DIA")) {
                    aderencias.add(createAderencia(meta, dia));
                    dia++;
                }
                final Aderencia aderencia = new Aderencia();
                aderencia.setDia(rSet.getInt("DIA"));
                aderencia.setRealizadas(rSet.getInt("REALIZADAS"));
                aderencia.setMeta(meta);
                aderencias.add(aderencia);
                dia++;

                if (rSet.isLast()) {
                    while (dia <= ultimoDia) {
                        aderencias.add(createAderencia(meta, dia));
                        dia++;
                    }
                }
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return aderencias;
    }

    @Override
    @Deprecated
    public List<Faixa> getQtPneusByFaixaPressao(final List<String> codUnidades, final List<String> status) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Faixa> faixas = null;
        final AfericaoDaoV2 afericaoDao = Injection.provideAfericaoDao();
        if (!codUnidades.get(0).equals("%")) {
            final Restricao restricao = afericaoDao.getRestricaoByCodUnidade(Long.parseLong(codUnidades.get(0)));
            final Integer base = (int) Math.round(restricao.getToleranciaCalibragem() * 100);
            faixas = criaFaixas(base, 30);
        } else {
            faixas = criaFaixas(0, 30);
        }
        final List<Integer> valores = new ArrayList<>();
        int naoAferidos = 0;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT COALESCE((((PRESSAO_ATUAL - PRESSAO_RECOMENDADA)/ PRESSAO_RECOMENDADA) *100)::TEXT, "
                            + "(((PRESSAO_ATUAL - PRESSAO_RECOMENDADA)/ PRESSAO_RECOMENDADA) *100)::TEXT, 'N') AS " +
                            "PORC  "
                            + "FROM PNEU  "
                            + "WHERE COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?]) "
                            + "ORDER BY 1 asc");
            stmt.setArray(1, PostgresUtils.listToArray(conn, codUnidades));
            stmt.setArray(2, PostgresUtils.listToArray(conn, status));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                if (rSet.getString("PORC").equals("N")) {
                    naoAferidos++;
                } else {
                    valores.add((int) rSet.getDouble("PORC"));
                }
            }
        } finally {
            close(conn, stmt, rSet);
        }

        final int totalValores = valores.size() + naoAferidos;
        populaFaixas(faixas, valores);
        setPorcentagemFaixas(faixas, totalValores);
        final Faixa faixa = new Faixa();
        faixa.setNaoAferidos(true);
        faixa.setTotalPneus(naoAferidos);
        faixa.setPorcentagem((double) naoAferidos / (double) totalValores);
        faixas.add(faixa);
        return faixas;
    }

    @Override
    public Map<StatusPneu, Integer> getQtdPneusByStatus(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<StatusPneu, Integer> statusPneus = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT P.STATUS, COUNT(P.CODIGO) " +
                    "FROM PNEU P " +
                    "WHERE P.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) " +
                    "GROUP BY P.STATUS " +
                    "ORDER BY 1;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                statusPneus.put(
                        StatusPneu.fromString(rSet.getString("STATUS")),
                        rSet.getInt("COUNT"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return statusPneus;
    }

    @Override
    public List<QuantidadeAfericao> getQtdAfericoesByTipoByData(@NotNull final List<Long> codUnidades,
                                                                @NotNull final Date dataInicial,
                                                                @NotNull final Date dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "PUBLIC.FUNC_PNEU_RELATORIO_QTD_AFERICOES_POR_TIPO_MEDICAO_COLETADA(?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setDate(2, dataInicial);
            stmt.setDate(3, dataFinal);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<QuantidadeAfericao> qtdAfericoes = new ArrayList<>();
                do {
                    qtdAfericoes.add(
                            new QuantidadeAfericao(
                                    rSet.getDate("DATA_REFERENCIA"),
                                    rSet.getString("DATA_REFERENCIA_FORMATADA"),
                                    rSet.getInt("QTD_AFERICAO_SULCO"),
                                    rSet.getInt("QTD_AFERICAO_PRESSAO"),
                                    rSet.getInt("QTD_AFERICAO_SULCO_PRESSAO")));
                } while (rSet.next());
                return qtdAfericoes;
            } else {
                throw new IllegalStateException("Erro ao buscar as informações de aferições realizadas para as " +
                        "unidades: " + codUnidades);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Map<TipoServico, Integer> getServicosEmAbertoByTipo(@NotNull final List<Long> codUnidades)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<TipoServico, Integer> servicosAbertos = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT AM.TIPO_SERVICO, COUNT(AM.TIPO_SERVICO) " +
                    "FROM AFERICAO_MANUTENCAO AM " +
                    "  JOIN VEICULO_PNEU VP " +
                    "    ON AM.COD_PNEU = VP.COD_PNEU AND AM.COD_UNIDADE = VP.COD_UNIDADE " +
                    "WHERE AM.DATA_HORA_RESOLUCAO IS NULL AND AM.COD_UNIDADE::TEXT LIKE ANY(ARRAY[?]) " +
                    "GROUP BY AM.TIPO_SERVICO;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                servicosAbertos.put(
                        TipoServico.fromString(rSet.getString("TIPO_SERVICO")),
                        rSet.getInt("COUNT"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return servicosAbertos;
    }

    @Override
    public StatusPlacasAfericao getStatusPlacasAfericao(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_RELATORIO_STATUS_PLACAS_AFERICAO(?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new StatusPlacasAfericao(
                        rSet.getInt("TOTAL_VENCIDAS"),
                        rSet.getInt("TOTAL_NO_PRAZO"));
            } else {
                throw new SQLException("Erro ao buscar o status das placas");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Map<TipoServico, Integer> getMediaTempoConsertoServicoPorTipo(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<TipoServico, Integer> resultados = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT AM.TIPO_SERVICO, " +
                    "  AVG(EXTRACT(EPOCH FROM (AM.DATA_HORA_RESOLUCAO - A.DATA_HORA)) / 3600)::INT AS MD_TEMPO_CONSERTO_HORAS " +
                    "FROM AFERICAO_MANUTENCAO AM JOIN AFERICAO A " +
                    "    ON A.CODIGO = AM.COD_AFERICAO " +
                    "WHERE AM.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) " +
                    "      AND AM.CPF_MECANICO IS NOT NULL " +
                    "GROUP BY AM.TIPO_SERVICO;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                resultados.put(
                        TipoServico.fromString(rSet.getString("TIPO_SERVICO")),
                        rSet.getInt("MD_TEMPO_CONSERTO_HORAS"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return resultados;
    }

    @Override
    public Map<String, Integer> getQtdKmRodadoComServicoEmAberto(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "PUBLIC.FUNC_PNEU_RELATORIO_QUANTIDADE_KMS_RODADOS_COM_SERVICOS_ABERTOS(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            final Map<String, Integer> resultados = new LinkedHashMap<>();
            while (rSet.next()) {
                resultados.put(
                        rSet.getString("PLACA_VEICULO"),
                        rSet.getInt("TOTAL_KM"));
            }
            return resultados;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<String, Integer> resultados = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM (SELECT " +
                    "        V.PLACA AS PLACA_VEICULO, " +
                    "        SUM(CASE " +
                    "            WHEN LEAST(P.ALTURA_SULCO_INTERNO, P.ALTURA_SULCO_EXTERNO, " +
                    "                       P.ALTURA_SULCO_CENTRAL_EXTERNO, P.ALTURA_SULCO_CENTRAL_INTERNO) " +
                    "                 < ERP.SULCO_MINIMO_DESCARTE " +
                    "              THEN 1 " +
                    "            ELSE 0 END) AS QT_PNEUS_ABAIXO_LIMITE " +
                    "      FROM VEICULO_PNEU VP JOIN PNEU P " +
                    "          ON P.CODIGO = VP.COD_PNEU AND VP.COD_UNIDADE = P.COD_UNIDADE " +
                    "        JOIN VEICULO V ON V.CODIGO = VP.COD_VEICULO AND V.STATUS_ATIVO" +
                    "        JOIN PNEU_RESTRICAO_UNIDADE ERP " +
                    "          ON ERP.COD_UNIDADE = VP.COD_UNIDADE " +
                    "      WHERE VP.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) " +
                    "      GROUP BY V.PLACA " +
                    "      ORDER BY 2 DESC) AS PLACA_PNEUS WHERE QT_PNEUS_ABAIXO_LIMITE > 0;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                resultados.put(rSet.getString("PLACA_VEICULO"),
                        rSet.getInt("QT_PNEUS_ABAIXO_LIMITE"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return resultados;
    }

    @Override
    public int getQtdPneusPressaoIncorreta(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final int total = 0;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(AM.COD_PNEU) AS TOTAL " +
                    "FROM AFERICAO_MANUTENCAO AM " +
                    "  JOIN VEICULO_PNEU VP " +
                    "    ON VP.COD_UNIDADE = AM.COD_UNIDADE AND AM.COD_PNEU = VP.COD_PNEU " +
                    "WHERE AM.COD_UNIDADE = ANY (ARRAY[?]) " +
                    "      AND (AM.TIPO_SERVICO = ? OR AM.TIPO_SERVICO = ?) " +
                    "      AND AM.DATA_HORA_RESOLUCAO IS NULL;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setString(2, TipoServico.CALIBRAGEM.asString());
            stmt.setString(3, TipoServico.INSPECAO.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("TOTAL");
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return total;
    }

    @NotNull
    @Override
    public List<SulcoPressao> getMenorSulcoEPressaoPneus(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<SulcoPressao> valores = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_RELATORIO_MENOR_SULCO_E_PRESSAO_PNEUS(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                valores.add(new SulcoPressao(
                        rSet.getLong("COD_PNEU"),
                        rSet.getString("COD_PNEU_CLIENTE"),
                        rSet.getDouble("MENOR_SULCO"),
                        rSet.getDouble("PRESSAO_ATUAL")));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return valores;
    }

    @Override
    public Map<String, Integer> getQtdPneusDescartadosPorMotivo(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<String, Integer> motivosDescarte = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  MMD.MOTIVO, COUNT(M.CODIGO) AS QUANTIDADE " +
                    "FROM MOVIMENTACAO M " +
                    "  JOIN MOVIMENTACAO_DESTINO MD " +
                    "    ON M.CODIGO = MD.COD_MOVIMENTACAO " +
                    "  JOIN UNIDADE U " +
                    "    ON U.CODIGO = M.COD_UNIDADE " +
                    "  JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMD " +
                    "    ON MMD.COD_EMPRESA = U.COD_EMPRESA AND MD.COD_MOTIVO_DESCARTE = MMD.CODIGO " +
                    "WHERE M.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) " +
                    "      AND MD.TIPO_DESTINO LIKE 'DESCARTE' " +
                    "GROUP BY MMD.MOTIVO " +
                    "ORDER BY 2 DESC;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                motivosDescarte.put(
                        rSet.getString("MOTIVO"),
                        rSet.getInt("QUANTIDADE"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return motivosDescarte;
    }

    @NotNull
    @Override
    public List<QtdDiasAfericoesVencidas> getQtdDiasAfericoesVencidas(
            @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            final List<QtdDiasAfericoesVencidas> qtdDiasAfericoesVencidas = new ArrayList<>();
            while (rSet.next()) {
                qtdDiasAfericoesVencidas.add(
                        new QtdDiasAfericoesVencidas(
                                rSet.getString("UNIDADE"),
                                rSet.getString("PLACA"),
                                rSet.getString("IDENTIFICADOR_FROTA"),
                                rSet.getBoolean("PODE_AFERIR_SULCO"),
                                rSet.getBoolean("PODE_AFERIR_PRESSAO"),
                                Optional.ofNullable(
                                        NullIf.equal(rSet.getInt("QTD_DIAS_AFERICAO_SULCO_VENCIDA"), 0)),
                                Optional.ofNullable(
                                        NullIf.equal(rSet.getInt("QTD_DIAS_AFERICAO_PRESSAO_VENCIDA"), 0))));
            }
            return qtdDiasAfericoesVencidas;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<QuantidadeAfericao> getQtdAfericoesRealizadasPorDiaByTipo(
            @NotNull final List<Long> codUnidades,
            final int diasRetroativosParaBuscar) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_AFERICAO_RELATORIO_QTD_AFERICOES_REALIZADAS_POR_DIA(?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.getLocalDateUtc());
            stmt.setInt(3, diasRetroativosParaBuscar);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<QuantidadeAfericao> afericoes = new ArrayList<>();
                do {
                    afericoes.add(new QuantidadeAfericao(
                            rSet.getObject("DATA", Date.class),
                            rSet.getString("DATA_FORMATADA"),
                            rSet.getInt("QTD_AFERICAO_SULCO"),
                            rSet.getInt("QTD_AFERICAO_PRESSAO"),
                            rSet.getInt("QTD_AFERICAO_SULCO_PRESSAO")));
                } while (rSet.next());
                return afericoes;
            } else {
                throw new IllegalStateException("Erro ao buscar as informações de aferições realizadas para as " +
                        "unidades: " + codUnidades);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getVencimentoDotReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getVencimentoDotStmt(conn, codUnidades, userToken);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getVencimentoDotCsv(@NotNull final OutputStream out,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getVencimentoDotStmt(conn, codUnidades, userToken);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getCpkPorMarcaModeloDimensaomCsv(@NotNull final OutputStream out,
                                                 @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getCpkPorMarcaModeloDimensaomStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getPneusComDesgasteIrregularStmt(@NotNull final Connection conn,
                                                               @NotNull final List<Long> codUnidades,
                                                               @Nullable final StatusPneu statusPneu) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(?, ? :: PNEU_STATUS_TYPE);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        if (statusPneu != null) {
            stmt.setString(2, statusPneu.asString());
        } else {
            stmt.setNull(2, SqlType.TEXT.asIntTypeJava());
        }
        return stmt;
    }

    @NotNull
    private PreparedStatement getStatusAtualPneusStmt(@NotNull final Connection conn,
                                                      @NotNull final List<Long> codUnidades) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_PNEU_RELATORIO_STATUS_ATUAL_PNEUS(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getVencimentoDotStmt(@NotNull final Connection conn,
                                                   @NotNull final List<Long> codUnidades,
                                                   @NotNull final String userToken) throws Throwable {
        final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(userToken, conn);
        final LocalDateTime dataHoraAtual = Now.getLocalDateTimeUtc()
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneId)
                .toLocalDateTime();
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_PNEU_RELATORIO_VALIDADE_DOT(?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataHoraAtual);
        return stmt;
    }

    @NotNull
    private PreparedStatement getPrevisaoTrocaStatement(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "select * from func_pneu_relatorio_previsao_troca(" +
                        "f_cod_unidades => ?," +
                        "f_status_pneu => ?," +
                        "f_data_final => ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, StatusPneu.EM_USO.asString());
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getPrevisaoTrocaConsolidadoStatement(@NotNull final Connection conn,
                                                                   @NotNull final List<Long> codUnidades,
                                                                   @NotNull final LocalDate dataInicial,
                                                                   @NotNull final LocalDate dataFinal)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "select * from func_pneu_relatorio_previsao_troca_consolidado(" +
                        "f_cod_unidades => ?," +
                        "f_status_pneu => ?," +
                        "f_data_inicial => ?," +
                        "f_data_final => ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, StatusPneu.EM_USO.asString());
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getAderenciaPlacasStatement(@NotNull final Connection conn,
                                                          @NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("" +
                "SELECT * FROM func_relatorio_pneu_aderencia_afericao(?,?,?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getPneusDescartadosStatement(@NotNull final Connection conn,
                                                           @NotNull final List<Long> codUnidades,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "select * from func_pneu_relatorio_pneus_descartados(" +
                        "f_cod_unidades => ?, " +
                        "f_data_inicial => ?, " +
                        "f_data_final => ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getDadosUltimaAfericaoStatement(@NotNull final Connection conn,
                                                              @NotNull final List<Long> codUnidades) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getResumoGeralPneusStatement(@NotNull final Connection conn,
                                                           @NotNull final List<Long> codUnidades,
                                                           @Nullable final String status) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
        if (status == null || status.isEmpty()) {
            stmt.setNull(2, Types.VARCHAR);
        } else {
            stmt.setString(2, status);
        }
        return stmt;
    }

    @NotNull
    private PreparedStatement getAfericoesAvulsasStatement(@NotNull final Connection conn,
                                                           @NotNull final List<Long> codUnidades,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getFarolAfericaoStatement(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_PNEU_RELATORIO_FAROL_AFERICAO(" +
                        "F_COD_UNIDADES := ?, " +
                        "F_DATA_INICIAL := ?, " +
                        "F_DATA_FINAL := ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getCpkPorMarcaModeloDimensaomStmt(@NotNull final Connection conn,
                                                                @NotNull final List<Long> codUnidades) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_PNEU_RELATORIO_CPK_MARCA_MODELO_DIMENSAO(F_COD_UNIDADES => ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getKmRodadoPorPneuPorVidaEmLinhas(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws SQLException {
        final PreparedStatement stmt =
                conn.prepareStatement("select * from func_pneu_relatorio_km_rodado_por_vida_linhas(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    private List<Faixa> populaFaixas(final List<Faixa> faixas, final List<Integer> valores) {
        Collections.sort(valores);
        int integer = 0;
        // percorre todas as faixas
        for (final Faixa faixa : faixas) {
            // percorre todos os valores
            for (int i = 0; i < valores.size(); i++) {
                integer = valores.get(i);
                // se a faixa começa com 0, veirica se é >= inicio e <= fim
                if (faixa.getInicio() == 0) {
                    if (integer >= faixa.getInicio() && integer <= faixa.getFim()) {
                        faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                        valores.remove(i);
                        i--;
                    }
                }
                // se a faixa for do lado negativo, a comparação se da de forma diferente >= inicio <fim
                else if (faixa.getInicio() < 0) {
                    // verifica se o valor esta apto a entrar na faixa
                    if (integer >= faixa.getInicio() && integer < faixa.getFim()) {
                        faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                        valores.remove(i);
                        i--;
                    }
                    // > inicio <= fim
                } else if (integer > faixa.getInicio() && integer <= faixa.getFim()) {
                    faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                    valores.remove(i);
                    i--;
                }
            }
        }
        Log.d(TAG, "Populadas: " + faixas.toString());
        return faixas;
    }

    private List<Faixa> criaFaixas(final int base, final int escala) {
        final List<Faixa> faixas = new ArrayList<>();
        // cria a primeira faixa de 0 até a restrição imposta pela empresa (3% por exemplo)
        Faixa faixa = new Faixa();
        faixa.setInicio(0);
        faixa.setFim(base);
        faixas.add(faixa);
        // cria a primeira faixas negativa, que vai de -3 a 0
        faixa = new Faixa();
        faixa.setInicio(base * -1);
        faixa.setFim(0);
        faixas.add(faixa);

        int inicio = base;
        int fim = base;

        // 1- verificar o próximo multiplo de 10 a partir da base(restricao)
        while (fim % 10 != 0) {
            fim++;
        }
        // cria a segunda faixa positiva, que vai de 3 a 10(calculado com o while acima)
        faixa = new Faixa();
        faixa.setInicio(inicio);
        faixa.setFim(fim);
        faixas.add(faixa);
        // cria a segunda faixa negativa, que vai de -10 a -3
        faixa = new Faixa();
        faixa.setInicio(fim * -1);
        faixa.setFim(inicio * -1);
        faixas.add(faixa);

        while (fim < 100) {
            inicio = fim;
            fim = inicio + escala;
            faixa = new Faixa();
            faixa.setInicio(inicio);
            faixa.setFim(fim);
            faixas.add(faixa);

            faixa = new Faixa();
            faixa.setInicio(fim * -1);
            faixa.setFim(inicio * -1);
            faixas.add(faixa);
        }

        Collections.sort(faixas, new CustomComparatorFaixas());
        return faixas;
    }

    @NotNull
    private List<Faixa> getFaixas(@NotNull final List<Double> valores) {
        Double minimo = (double) 0;
        final Double cota = (valores.get(0) / 5) + 1;
        Double maximo = cota;
        final int totalPneus = valores.size();
        final List<Faixa> faixas = new ArrayList<>();
        //cria as faixas
        while (minimo < valores.get(0)) {
            final Faixa faixa = new Faixa();
            faixa.setInicio(minimo);
            faixa.setFim(maximo);
            minimo = maximo;
            maximo = maximo + cota;
            faixas.add(faixa);
        }
        //soma cada sulco para a sua devida faixa
        for (final Faixa faixa : faixas) {
            for (int i = 0; i < valores.size(); i++) {
                if (valores.get(i) >= faixa.getInicio() && valores.get(i) < faixa.getFim()) {
                    faixa.setTotalPneus(faixa.getTotalPneus() + 1);
                    valores.remove(i);
                    i--;
                }
            }
            faixa.setPorcentagem((double) faixa.getTotalPneus() / totalPneus);
        }
        // cria a faixa de itens não aferidos, com o que sobrou da lista valores
        final Faixa faixa = new Faixa();
        faixa.setNaoAferidos(true);
        faixa.setTotalPneus(valores.size());
        faixa.setPorcentagem((double) valores.size() / totalPneus);
        faixas.add(faixa);

        return faixas;
    }

    private Aderencia createAderencia(final double meta, final int dia) {
        final Aderencia aderencia = new Aderencia();
        aderencia.setDia(dia);
        aderencia.setMeta(meta);
        return aderencia;
    }

    private void setPorcentagemFaixas(final List<Faixa> faixas, final int total) {
        for (final Faixa faixa : faixas) {
            if (faixa.getTotalPneus() == 0) {
                faixa.setPorcentagem(0);
            } else {
                final double porcentagem = (double) faixa.getTotalPneus() / total;
                faixa.setPorcentagem(porcentagem);
            }
        }
    }

    //ordena as faixas pelo inicio de cada uma
    private class CustomComparatorFaixas implements Comparator<Faixa> {
        /**
         * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
         */
        @Override
        public int compare(final Faixa o1, final Faixa o2) {
            return Double.compare(o1.getInicio(), o2.getInicio());
        }
    }
}