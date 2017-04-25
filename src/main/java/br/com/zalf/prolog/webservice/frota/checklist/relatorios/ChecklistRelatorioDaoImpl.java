package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportConverter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

/**
 * Created by luiz on 25/04/17.
 */
public class ChecklistRelatorioDaoImpl extends DatabaseConnection implements ChecklistRelatorioDao {

    @Override
    public void getChecklistsRealizadosDiaCsv(@NotNull OutputStream outputStream,
                                              @NotNull Long codUnidade,
                                              @NotNull Date dataInicial,
                                              @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getCheckilistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getChecklistsRealizadosDiaReport(@NotNull Long codUnidade,
                                                   @NotNull Date dataInicial,
                                                   @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getCheckilistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getExtratoChecklistsRealizadosDiaCsv(@NotNull OutputStream outputStream,
                                                     @NotNull Long codUnidade,
                                                     @NotNull Date dataInicial,
                                                     @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoCheckilistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getExtratoChecklistsRealizadosDiaReport(@NotNull Long codUnidade,
                                                          @NotNull Date dataInicial,
                                                          @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoCheckilistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

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
    private PreparedStatement getCheckilistRealizadosDia(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
        throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT c.data_hora::date as \"DATA\",\n" +
                "sum(case when c.tipo = 'S' then 1 else 0 end) as \"CHECKS SAÍDA\",\n" +
                "sum(case when c.tipo = 'R' then 1 else 0 end) as \"CHECKS RETORNO\",\n" +
                "count(c.data_hora::date) as \"TOTAL CHECKS\",\n" +
                "dia_mapas.total_mapas_dia,\n" +
                "trunc((count(c.data_hora::date)::float/dia_mapas.total_mapas_dia)*100) as \"aderencia(%)\"\n" +
                "FROM checklist c\n" +
                "LEFT JOIN (SELECT m.data as data_mapa, count(m.mapa) as total_mapas_dia\n" +
                "FROM mapa m\n" +
                "JOIN veiculo v on v.placa = m.placa\n" +
                "WHERE m.cod_unidade = ? and m.data BETWEEN ? and ?\n" +
                "GROUP BY m.data\n" +
                "ORDER BY m.data asc) as dia_mapas ON dia_mapas.data_mapa = c.data_hora::date\n" +
                "WHERE c.cod_unidade = ? and c.data_hora::date BETWEEN ? and ?\n" +
                "GROUP BY c.data_hora::date, dia_mapas.total_mapas_dia\n" +
                "ORDER BY c.data_hora::date;");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setLong(4, codUnidade);
        stmt.setDate(5, dataInicial);
        stmt.setDate(6, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getExtratoCheckilistRealizadosDia(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT c.data_hora::date as \"DATA\",\n" +
                "c.placa_veiculo,\n" +
                "sum(case when c.tipo = 'S' then 1 else 0 end) as \"CHECKS SAÍDA\",\n" +
                "sum(case when c.tipo = 'R' then 1 else 0 end) as \"CHECKS RETORNO\"\n" +
                "FROM checklist c\n" +
                "LEFT JOIN\n" +
                "(SELECT m.data as data_mapa, m.mapa, m.placa\n" +
                "FROM mapa m\n" +
                "JOIN veiculo v on v.placa = m.placa\n" +
                "WHERE m.cod_unidade = ? and m.data BETWEEN ? and ?\n" +
                "ORDER BY m.data asc) as dia_mapas ON dia_mapas.data_mapa = c.data_hora::date and dia_mapas.placa = c.placa_veiculo\n" +
                "WHERE c.cod_unidade = ? and c.data_hora::date BETWEEN ? and ?\n" +
                "GROUP BY 1, 2\n" +
                "ORDER BY c.data_hora::date;");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setLong(4, codUnidade);
        stmt.setDate(5, dataInicial);
        stmt.setDate(6, dataFinal);
        return stmt;
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