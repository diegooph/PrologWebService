package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.OLD.DeprecatedControleIntervaloDaoImpl_2;
import br.com.zalf.prolog.webservice.gente.controlejornada.OLD.DeprecatedControleIntervaloDao_2;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.RelatorioTotaisPorTipoIntervalo;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleJornadaRelatorioDaoImpl extends DatabaseConnection implements ControleJornadaRelatoriosDao {
    private static final String TAG = ControleJornadaRelatorioDaoImpl.class.getSimpleName();

    @Override
    public void getMarcacoesDiariasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getMarcacoesDiariasReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAderenciaIntervalosDiariaReport(Long codUnidade, Date dataInicial, Date dataFinal)
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getAderenciaIntervalosColaboradorCsv(OutputStream out, Long codUnidade, Date dataInicial, Date
            dataFinal, String cpf)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosColaboradorStmt(codUnidade, dataInicial, dataFinal, conn, cpf);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAderenciaIntervalosColaboradorReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosColaboradorStmt(codUnidade, dataInicial, dataFinal, conn, cpf);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
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
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                            @NotNull final String codTipoIntervalo,
                                                            @NotNull final String cpf,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_INTERVALO_FOLHA_PONTO(?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            if (codTipoIntervalo.equals("%")) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.parseLong(codTipoIntervalo));
            }
            if (cpf.equals("%")) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, Long.parseLong(cpf));
            }
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(6, zoneId.getId());
            rSet = stmt.executeQuery();

            final DeprecatedControleIntervaloDao_2 dao = new DeprecatedControleIntervaloDaoImpl_2();
            return ControleJornadaRelatorioConverter.createFolhaPontoRelatorio(
                    rSet,
                    dao.getTiposIntervalosByUnidade(codUnidade, true, false),
                    dataInicial,
                    dataFinal,
                    zoneId);
        } finally {
            closeConnection(conn, stmt, rSet);
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
            closeConnection(conn, stmt, rSet);
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
            closeConnection(conn, stmt, rSet);
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
            stmt = getTotalTempoByTipoIntervaloStmt(conn, codUnidade, codTipoIntervalo, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            final DeprecatedControleIntervaloDao_2 dao = new DeprecatedControleIntervaloDaoImpl_2();
            new CsvWriter
                    .Builder(out)
                    .withCsvReport(new RelatorioTotaisPorTipoIntervalo(
                            rSet,
                            dao.getTiposIntervalosByUnidade(codUnidade, true,false),
                            codTipoIntervalo.equals("%") ? null : Long.parseLong(codTipoIntervalo)))
                    .build()
                    .write();
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getTotalTempoByTipoIntervaloReport(@NotNull final Long codUnidade,
                                                     @NotNull final String codTipoIntervalo,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTotalTempoByTipoIntervaloStmt(conn, codUnidade, codTipoIntervalo, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getTotalTempoByTipoIntervaloStmt(@NotNull final Connection conn,
                                                               @NotNull final Long codUnidade,
                                                               @NotNull final String codTipoIntervalo,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_INTERVALOS_GET_TOTAL_TEMPO_POR_TIPO_INTERVALO(?, ?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        if (codTipoIntervalo.equals("%")) {
            stmt.setNull(2, Types.BIGINT);
        } else {
            stmt.setLong(2, Long.parseLong(codTipoIntervalo));
        }
        stmt.setObject(3, dataInicial.atTime(LocalTime.MIN));
        stmt.setObject(4, dataFinal.atTime(LocalTime.MAX));
        stmt.setObject(5, Now.localDateTimeUtc());
        return stmt;
    }

    @NotNull
    private PreparedStatement getAderenciaIntervalosColaboradorStmt(Long codUnidade, Date dataInicial, Date
            dataFinal, Connection conn, String cpf) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM func_relatorio_aderencia_intervalo_colaborador(?,?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        stmt.setString(4, cpf);
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
    private PreparedStatement getAderenciaIntervalosDiariaStmt(Long codUnidade, Date dataInicial, Date dataFinal,
                                                               Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM func_relatorio_aderencia_intervalo_dias(?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

    @NotNull
    private PreparedStatement getMarcacoesDiariasStmt(Long codUnidade, Date dataInicial, Date dataFinal, String cpf,
                                                Connection conn) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        stmt.setString(4, cpf);
        return stmt;
    }

    @NotNull
    private PreparedStatement getIntervalosMapasStmt(Long codUnidade, Date dataInicial, Date dataFinal, Connection conn) throws SQLException {
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
}