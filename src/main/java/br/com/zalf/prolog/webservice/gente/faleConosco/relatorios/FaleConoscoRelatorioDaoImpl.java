package br.com.zalf.prolog.webservice.gente.faleConosco.relatorios;

import br.com.zalf.prolog.webservice.commons.Report;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportTransformer;

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
public class FaleConoscoRelatorioDaoImpl extends DatabaseConnection implements FaleConoscoRelatorioDao{

    @Override
    public void getResumoCsv(Long codUnidade, OutputStream outputStream, Date dataInicial, Date dataFinal)
            throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getResumoReport(Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getResumoStatement(Connection conn, long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT dados.total_geral as \"TOTAL\",\n" +
                "  dados.total_sugestao as \"SUGESTÕES\",\n" +
                "  dados.total_reclamacao as \"RECLAMAÇÕES\",\n" +
                "  dados.total_respondidos as \"TOTAL RESPONDIDOS\",\n" +
                "  round((dados.total_respondidos / dados.total_geral::float)*100) || '%' as \"% RESPONDIDOS\",\n" +
                "  round((dados.total_sugestao / dados.total_geral::float)*100) || '%' as \"% SUGESTÃO\",\n" +
                "  dados.total_sugestao_respondidos as \"SUGESTÕES RESPONDIDAS\",\n" +
                "  round((dados.total_sugestao_respondidos / dados.total_geral::float)*100) || '%' as \"% SUGESTÃO RESPONDIDAS\",\n" +
                "  round((dados.total_reclamacao / dados.total_geral::float)*100) || '%' as \"% RECLAMAÇÃO\",\n" +
                "  dados.total_reclamacao_respondidos as \"RECLAMAÇÕES RESPONDIDAS\",\n" +
                "  round((dados.total_reclamacao_respondidos / dados.total_geral::float)*100) || '%' as \"% RECLAMAÇÃO RESPONDIDAS\"\n" +
                "FROM\n" +
                "(SELECT\n" +
                "  sum(case when categoria = 'S' then 1 end) as total_sugestao,\n" +
                "  sum(case when categoria = 'S' and data_hora_feedback is not null then 1 end) as total_sugestao_respondidos,\n" +
                "  sum(case when categoria = 'R' then 1 end) as total_reclamacao,\n" +
                "  sum(case when categoria = 'R' and data_hora_feedback is not null then 1 end) as total_reclamacao_respondidos,\n" +
                "  count(data_hora) as total_geral,\n" +
                "  count(fc.data_hora_feedback) as total_respondidos,\n" +
                "  trunc(extract(epoch from avg (data_hora_feedback - data_hora)) / 86400) as md_dias_feedback\n" +
                "FROM fale_conosco fc\n" +
                "WHERE cod_unidade= ? and data_hora BETWEEN ? and ?) as dados");
        stmt.setLong(1, codUnidade);
        stmt.setTimestamp(2, DateUtils.toTimestamp(dataInicial));
        stmt.setTimestamp(3, DateUtils.toTimestamp(dataFinal));
        return stmt;
    }
}
