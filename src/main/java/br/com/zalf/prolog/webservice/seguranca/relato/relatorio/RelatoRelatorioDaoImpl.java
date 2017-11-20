package br.com.zalf.prolog.webservice.seguranca.relato.relatorio;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 20/11/2017.
 */
public class RelatoRelatorioDaoImpl extends DatabaseConnection implements RelatoRelatorioDao {

    @Override
    public void getRelatosEstratificadosCsv(Long codUnidade, Date dataInicial, Date dataFinal, String equipe, OutputStream out)
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

    @Override
    public Report getRelatosEstratificadosReport(Long codUnidade, Date dataInicial, Date dataFinal, String equipe)
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

    public PreparedStatement getRelatosEstratificadosStmt (Long codUnidade, Date dataInicial, Date dataFinal, String equipe, Connection conn)
            throws SQLException {
        PreparedStatement stmt = null;
        stmt = conn.prepareStatement("SELECT * FROM func_relatorio_extrato_relatos(?,?,?,?)");
        stmt.setDate(1, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
        stmt.setLong(3, codUnidade);
        stmt.setString(4, equipe);
        return stmt;
    }

}
