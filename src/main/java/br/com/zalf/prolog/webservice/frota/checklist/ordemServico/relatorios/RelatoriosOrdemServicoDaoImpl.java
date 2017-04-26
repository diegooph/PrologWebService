package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportConverter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

/**
 * Created by luiz on 26/04/17.
 */
public class RelatoriosOrdemServicoDaoImpl extends DatabaseConnection implements RelatoriosOrdemServicoDao {

    @Override
    public void getItensMaiorQuantidadeNokCsv(@NotNull OutputStream outputStream,
                                              @NotNull Long codUnidade,
                                              @NotNull Date dataInicial,
                                              @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getItensMaiorQuantidadeNok(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getItensMaiorQuantidadeNokReport(@NotNull Long codUnidade,
                                                   @NotNull Date dataInicial,
                                                   @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getItensMaiorQuantidadeNok(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getItensMaiorQuantidadeNok(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT cp.pergunta, cap.alternativa, prioridade,\n" +
                "sum( case when cr.resposta <> 'OK' then 1 else 0 end ) as contagem,\n" +
                "count(cp.pergunta) as total,\n" +
                "trunc((sum( case when cr.resposta <> 'OK' then 1 else 0 end ) / count(cp.pergunta)::float) * 100) || '%' as proporcao\n" +
                "FROM checklist c\n" +
                "JOIN checklist_respostas cr ON c.cod_unidade = cr.cod_unidade AND cr.cod_checklist_modelo = c.cod_checklist_modelo\n" +
                "JOIN checklist_perguntas cp ON cp.cod_unidade = C.cod_unidade AND cp.codigo = CR.cod_pergunta AND cp.cod_checklist_modelo = cr.cod_checklist_modelo\n" +
                "JOIN veiculo v ON v.placa::text = c.placa_veiculo::text\n" +
                "JOIN checklist_alternativa_pergunta cap ON cap.cod_unidade = cp.cod_unidade AND cap.cod_checklist_modelo = cp.cod_checklist_modelo\n" +
                "AND cap.cod_pergunta = cp.codigo AND cap.codigo = cr.cod_alternativa\n" +
                "AND cr.cod_checklist = c.codigo AND cr.cod_pergunta = cp.codigo AND cr.cod_alternativa = cap.codigo\n" +
                "WHERE c.cod_unidade = ? and c.data_hora BETWEEN ? and ?\n" +
                "GROUP BY 1, 2, 3\n" +
                "ORDER BY trunc((sum( case when cr.resposta <> 'OK' then 1 else 0 end ) / count(cp.pergunta)::float) * 100) desc");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }
}