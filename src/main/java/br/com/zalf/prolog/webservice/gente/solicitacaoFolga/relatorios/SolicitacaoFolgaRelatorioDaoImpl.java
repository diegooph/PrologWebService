package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 02/05/17.
 */
public class SolicitacaoFolgaRelatorioDaoImpl extends DatabaseConnection implements SolicitacaoFolgaRelatorioDao {

    @Override
    public void getResumoFolgasConcedidasCsv(Long codUnidade, OutputStream outputStream, Date dataInicial, Date dataFinal)
            throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoFolgasConcedidasStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getResumoFolgasConcedidasReport(Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoFolgasConcedidasStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getResumoFolgasConcedidasStatement(Connection conn, long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT TO_CHAR(AD.DATA, 'DD/MM/YYYY') AS \"DATA SOLICITAÇÃO\",\n" +
                "  count(codigo) AS \"TOTAL CONCEDIDAS\",\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'MANHA' THEN 1 ELSE 0 end) as \"CONCEDIDAS MANHÃ\",\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'TARDE' THEN 1 ELSE 0 end) as \"CONCEDIDAS TARTE\",\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'NOITE' THEN 1 ELSE 0 end) as \"CONCEDIDAS NOITE\",\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'DIA_TODO' THEN 1 ELSE 0 end) as \"CONCEDIDAS DIA INTEIRO\"\n" +
                "FROM aux_data ad\n" +
                "  left JOIN solicitacao_folga sf on ad.data = sf.data_solicitacao AND SF.status = 'AUTORIZADA'\n" +
                "  left JOIN  colaborador c on c.cpf = sf.cpf_colaborador and c.cod_unidade = ?\n" +
                "  WHERE AD.DATA >= (? AT TIME ZONE ?) and AD.DATA <= (? AT TIME ZONE ?) \n" +
                "  GROUP BY 1;");
        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setString(3, zoneId);
        stmt.setDate(4, DateUtils.toSqlDate(dataFinal));
        stmt.setString(5, zoneId);
        return stmt;
    }

}
