package br.com.zalf.prolog.webservice.gente.solicitacaoFolga.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportConverter;

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
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getResumoFolgasConcedidasStatement(Connection conn, long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT ad.data,\n" +
                "  count(codigo) AS total_concedidas,\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'MANHA' THEN 1 ELSE 0 end) as concedidas_manha,\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'TARDE' THEN 1 ELSE 0 end) as concedidas_tarde,\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'NOITE' THEN 1 ELSE 0 end) as concedidas_noite,\n" +
                "  sum(CASE WHEN sf.periodo LIKE 'DIA_TODO' THEN 1 ELSE 0 end) as concedidas_dia_todo\n" +
                "FROM aux_data ad\n" +
                "  left JOIN solicitacao_folga sf on ad.data = sf.data_solicitacao AND SF.status = 'AUTORIZADA'\n" +
                "  left JOIN  colaborador c on c.cpf = sf.cpf_colaborador and c.cod_unidade = ?\n" +
                "  WHERE ad.data BETWEEN ? and ? \n" +
                "  GROUP BY 1;");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

}
