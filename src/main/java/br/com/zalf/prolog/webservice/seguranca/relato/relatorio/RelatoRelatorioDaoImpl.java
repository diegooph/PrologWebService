package br.com.zalf.prolog.webservice.seguranca.relato.relatorio;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.seguranca.relato.model.RelatoPendente;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by Zart on 20/11/2017.
 */
public class RelatoRelatorioDaoImpl extends DatabaseConnection implements RelatoRelatorioDao {

    @Override
    public void getRelatosEstratificadosCsv(final Long codUnidade, final Date dataInicial, final Date dataFinal, final String equipe, final OutputStream out)
            throws SQLException, IOException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = getRelatosEstratificadosStmt(codUnidade, dataInicial, dataFinal, equipe, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

    }

    @NotNull
    @Override
    public Report getRelatosEstratificadosReport(final Long codUnidade, final Date dataInicial, final Date dataFinal, final String equipe)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRelatosEstratificadosStmt(codUnidade, dataInicial, dataFinal, equipe, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

    }

    @Override
    public int getQtdRelatosRealizadosHoje(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(R.CODIGO) AS TOTAL FROM RELATO R " +
                                                 "WHERE R.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) " +
                                                 "AND R.DATA_HORA_DATABASE::DATE = (? AT TIME ZONE (SELECT TIMEZONE " +
                                                 "FROM func_get_time_zone_unidade(R.COD_UNIDADE)));");
            stmt.setArray(1, PostgresUtils.listLongToArray(conn, codUnidades));
            stmt.setObject(2, LocalDate.now(Clock.systemUTC()));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("total");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return 0;
    }

    @NotNull
    @Override
    public RelatoPendente getQtdRelatosPendentesByStatus(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_RELATO_RELATORIO_QTD_RELATOS_PENDENTES_BY_STATUS(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new RelatoPendente(
                        rSet.getInt("QTD_PENDENTES_CLASSIFICACAO"),
                        rSet.getInt("QTD_PENDENTES_FECHAMENTO"));
            } else {
                throw new SQLException("Erro ao buscar os relatos pendentes");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private PreparedStatement getRelatosEstratificadosStmt(final Long codUnidade, final Date dataInicial, final Date dataFinal, final String equipe, final Connection conn)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_extrato_relatos(?,?,?,?)");
        stmt.setDate(1, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
        stmt.setLong(3, codUnidade);
        stmt.setString(4, equipe);
        return stmt;
    }
}