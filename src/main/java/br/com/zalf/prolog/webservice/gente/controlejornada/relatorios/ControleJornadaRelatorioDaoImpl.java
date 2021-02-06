package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.Filtros;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.RelatorioTotaisPorTipoIntervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacaoDao;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleJornadaRelatorioDaoImpl extends DatabaseConnection implements ControleJornadaRelatoriosDao {
    private static final String TAG = ControleJornadaRelatorioDaoImpl.class.getSimpleName();

    @Override
    public void getMarcacoesDiariasCsv(final OutputStream out, final Long codUnidade, final Date dataInicial, final Date dataFinal, final String cpf)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMarcacoesDiariasStmt(codUnidade, dataInicial, dataFinal, cpf, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getMarcacoesDiariasReport(final Long codUnidade, final Date dataInicial, final Date dataFinal, final String cpf)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMarcacoesDiariasStmt(codUnidade, dataInicial, dataFinal, cpf, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getIntervalosMapasCsv(final OutputStream out, final Long codUnidade, final Date dataInicial, final Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosMapasStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getIntervalosMapasReport(final Long codUnidade, final Date dataInicial, final Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosMapasStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getAderenciaIntervalosDiariaCsv(final OutputStream out, final Long codUnidade, final Date dataInicial, final Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosDiariaStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAderenciaIntervalosDiariaReport(final Long codUnidade, final Date dataInicial, final Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosDiariaStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getAderenciaMarcacoesColaboradoresCsv(@NotNull final OutputStream out,
                                                      @NotNull final Long codUnidade,
                                                      @Nullable final Long cpf,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaMarcacoesColaboradoresStmt(conn, codUnidade, cpf, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAderenciaMarcacoesColaboradoresReport(@NotNull final Long codUnidade,
                                                           @Nullable final Long cpf,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaMarcacoesColaboradoresStmt(conn, codUnidade, cpf, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getRelatorioPadraoPortaria1510Csv(@NotNull final OutputStream out,
                                                  @NotNull final Long codUnidade,
                                                  @NotNull final Long codTipoIntervalo,
                                                  @NotNull final String cpf,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRelatorioPadraoPortaria1510Stmt(codUnidade, codTipoIntervalo, cpf, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                            @NotNull final String codTipoIntervalo,
                                                            @NotNull final String cpf,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal,
                                                            final boolean apenasColaboradoresAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(?, ?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            if (Filtros.isFiltroTodos(codTipoIntervalo)) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.parseLong(codTipoIntervalo));
            }
            if (Filtros.isFiltroTodos(cpf)) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, Long.parseLong(cpf));
            }
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            if (apenasColaboradoresAtivos) {
                stmt.setBoolean(6, true);
            } else {
                stmt.setNull(6, Types.BOOLEAN);
            }
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(7, zoneId.getId());
            rSet = stmt.executeQuery();

            final TipoMarcacaoDao dao = Injection.provideTipoMarcacaoDao();
            return ControleJornadaRelatorioConverter.createFolhaPontoRelatorio(
                    rSet,
                    dao.getTiposMarcacoes(codUnidade, false, false),
                    zoneId);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<FolhaPontoJornadaRelatorio> getFolhaPontoJornadaRelatorio(
            @NotNull final Long codUnidade,
            @NotNull final String codTipoIntervalo,
            @NotNull final String cpf,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal,
            final boolean apenasColaboradoresAtivos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_MARCACAO_RELATORIO_FOLHA_PONTO_JORNADA(?, ?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            if (Filtros.isFiltroTodos(codTipoIntervalo)) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.parseLong(codTipoIntervalo));
            }
            if (Filtros.isFiltroTodos(cpf)) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, Long.parseLong(cpf));
            }
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            if (apenasColaboradoresAtivos) {
                stmt.setBoolean(6, true);
            } else {
                stmt.setNull(6, Types.BOOLEAN);
            }
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(7, zoneId.getId());

            rSet = stmt.executeQuery();
            final TipoMarcacaoDao tipoMarcacaoDao = Injection.provideTipoMarcacaoDao();
            return ControleJornadaRelatorioConverter.createFolhaPontoJornadaRelatorio(
                    rSet,
                    tipoMarcacaoDao.getTiposMarcacoes(codUnidade, false, false),
                    tipoMarcacaoDao.getFormulaCalculoJornada(codUnidade),
                    zoneId);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getMarcacoesComparandoEscalaDiariaReport(@NotNull final Long codUnidade,
                                                           @NotNull final Long codTipoIntervalo,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMarcacoesComparandoEscalaDiariaStmt(codUnidade, codTipoIntervalo, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getMarcacoesComparandoEscalaDiariaCsv(@NotNull final OutputStream out,
                                                      @NotNull final Long codUnidade,
                                                      @NotNull final Long codTipoIntervalo,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMarcacoesComparandoEscalaDiariaStmt(codUnidade, codTipoIntervalo, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getTotalTempoByTipoIntervaloCsv(@NotNull final OutputStream out,
                                                @NotNull final Long codUnidade,
                                                @NotNull final String codTipoIntervalo,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTotalTempoByTipoIntervaloStmt(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            final TipoMarcacaoDao dao = Injection.provideTipoMarcacaoDao();
            new CsvWriter
                    .Builder(out)
                    .withCsvReport(new RelatorioTotaisPorTipoIntervalo(
                            rSet,
                            dao.getTiposMarcacoes(
                                    codUnidade,
                                    /* Se o filtro de tipo do relatório for por todos, então trazemos apenas os tipos
                                    ativos. Ou seja, se for por todos, o colaborador não terá a visão poluída com,
                                    talvez, diversos tipos já inativados. Porém, se ele filtrar especificamente por um
                                    inativado, esse tipo irá retornar no relatório. */
                                    Filtros.isFiltroTodos(codTipoIntervalo),
                                    false),
                            Filtros.isFiltroTodos(codTipoIntervalo) ? null : Long.parseLong(codTipoIntervalo)))
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getMarcacoesExportacaoGenericaCsv(@NotNull final OutputStream out,
                                                  @NotNull final Long codUnidade,
                                                  @Nullable final Long codTipoIntervalo,
                                                  @Nullable final Long codColaborador,
                                                  final boolean apenasMarcacoesAtivas,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMarcacoesExportacaoGenericaCsv(
                    conn,
                    codUnidade,
                    codTipoIntervalo,
                    codColaborador,
                    apenasMarcacoesAtivas,
                    dataInicial,
                    dataFinal);
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

    private PreparedStatement getTotalTempoByTipoIntervaloStmt(@NotNull final Connection conn,
                                                               @NotNull final Long codUnidade,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_MARCACAO_GET_TEMPO_TOTAL_POR_TIPO_MARCACAO(?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getAderenciaMarcacoesColaboradoresStmt(@NotNull final Connection conn,
                                                                     @NotNull final Long codUnidade,
                                                                     @Nullable final Long cpf,
                                                                     @NotNull final LocalDate dataInicial,
                                                                     @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_MARCACAO_RELATORIO_ADERENCIA_MARCACOES_COLABORADORES_MAPA(" +
                        "F_COD_UNIDADE := ?," +
                        "F_CPF := ?," +
                        "F_DATA_INICIAL := ?," +
                        "F_DATA_FINAL := ?)");
        stmt.setLong(1, codUnidade);
        if (cpf != null) {
            stmt.setLong(2, cpf);
        } else {
            stmt.setNull(2, SqlType.BIGINT.asIntTypeJava());
        }
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getRelatorioPadraoPortaria1510Stmt(@NotNull final Long codUnidade,
                                                                 @NotNull final Long codTipoIntervalo,
                                                                 @NotNull final String cpf,
                                                                 @NotNull final LocalDate dataInicial,
                                                                 @NotNull final LocalDate dataFinal,
                                                                 @NotNull final Connection conn) throws SQLException {
        Preconditions.checkNotNull(codUnidade);
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM func_relatorio_intervalo_portaria_1510_tipo_3(?, ?, ?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codTipoIntervalo);
        if (!cpf.equals("%")) {
            stmt.setLong(3, Long.parseLong(cpf));
        } else {
            stmt.setNull(3, Types.BIGINT);
        }
        stmt.setObject(4, dataInicial);
        stmt.setObject(5, dataFinal);
        stmt.setString(6, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
        return stmt;
    }

    @NotNull
    private PreparedStatement getAderenciaIntervalosDiariaStmt(final Long codUnidade, final Date dataInicial, final Date dataFinal,
                                                               final Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM func_relatorio_aderencia_intervalo_dias(?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

    @NotNull
    private PreparedStatement getMarcacoesDiariasStmt(final Long codUnidade, final Date dataInicial, final Date dataFinal, final String cpf,
                                                      final Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        stmt.setString(4, cpf);
        return stmt;
    }

    @NotNull
    private PreparedStatement getIntervalosMapasStmt(final Long codUnidade, final Date dataInicial, final Date dataFinal, final Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_intervalos_mapas(?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

    @NotNull
    private PreparedStatement getMarcacoesComparandoEscalaDiariaStmt(@NotNull final Long codUnidade,
                                                                     @NotNull final Long codTipoIntervalo,
                                                                     @NotNull final LocalDate dataInicial,
                                                                     @NotNull final LocalDate dataFinal,
                                                                     @NotNull final Connection conn) throws SQLException {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_INTERVALO_ESCALA_DIARIA(?, ?, ?, ?, ?)");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codTipoIntervalo);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        stmt.setString(5, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
        return stmt;
    }

    @NotNull
    private PreparedStatement getMarcacoesExportacaoGenericaCsv(@NotNull final Connection conn,
                                                                @NotNull final Long codUnidade,
                                                                @Nullable final Long codTipoIntervalo,
                                                                @Nullable final Long codColaborador,
                                                                final boolean apenasMarcacoesAtivas,
                                                                @NotNull final LocalDate dataInicial,
                                                                @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_MARCACAO_RELATORIO_EXPORTACAO_GENERICA(" +
                        "F_COD_UNIDADE             := ?, " +
                        "F_COD_TIPO_INTERVALO      := ?, " +
                        "F_COD_COLABORADOR         := ?, " +
                        "F_APENAS_MARCACOES_ATIVAS := ?, " +
                        "F_DATA_INICIAL            := ?, " +
                        "F_DATA_FINAL              := ?);");
        stmt.setLong(1, codUnidade);
        bindValueOrNull(stmt, 2, codTipoIntervalo, SqlType.BIGINT);
        bindValueOrNull(stmt, 3, codColaborador, SqlType.BIGINT);
        if (apenasMarcacoesAtivas) {
            stmt.setBoolean(4, true);
        } else {
            stmt.setNull(4, Types.BOOLEAN);
        }
        stmt.setObject(5, dataInicial);
        stmt.setObject(6, dataFinal);
        return stmt;
    }
}