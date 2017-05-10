package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportTransformer;

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
            return ReportTransformer.createReport(rSet);
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
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getTempoRealizacaoChecklistMotoristaCsv(@NotNull OutputStream outputStream,
                                                        @NotNull Long codUnidade,
                                                        @NotNull Date dataInicial,
                                                        @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTempoRealizacaoChecklistMotorista(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getTempoRealizacaoChecklistMotoristaReport(@NotNull Long codUnidade,
                                                             @NotNull Date dataInicial,
                                                             @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTempoRealizacaoChecklistMotorista(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
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
    private PreparedStatement getTempoRealizacaoChecklistMotorista(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT co.nome as nome,\n" +
                "f.nome as funcao,\n" +
                " sum ( case when c.tipo = 'S' then 1 else 0 end ) as qt_checks_saida,\n" +
                " sum ( case when c.tipo = 'R' then 1 else 0 end ) as qt_checks_retorno,\n" +
                " count(c.tipo) as qt_total_checks_realizados,\n" +
                "round(avg(c.tempo_realizacao)/60000) as md_minutos_realizacao\n" +
                "FROM checklist c\n" +
                " JOIN colaborador co on co.cpf = c.cpf_colaborador\n" +
                " JOIN funcao f on f.codigo = co.cod_funcao and f.cod_empresa = \n" +
                "co.cod_empresa\n" +
                "WHERE c.cod_unidade = ? and c.data_hora BETWEEN ? and ?\n" +
                " GROUP BY co.nome, f.nome\n" +
                "ORDER BY co.nome");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }
}