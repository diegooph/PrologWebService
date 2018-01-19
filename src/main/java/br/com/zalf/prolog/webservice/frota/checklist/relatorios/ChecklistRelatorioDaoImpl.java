package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

/**
 * Created by luiz on 25/04/17.
 */
public class ChecklistRelatorioDaoImpl extends DatabaseConnection implements ChecklistRelatorioDao {

    public ChecklistRelatorioDaoImpl() {

    }

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
            stmt = getChecklistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
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
            stmt = getChecklistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
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
            stmt = getExtratoChecklistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
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
            stmt = getExtratoChecklistRealizadosDia(conn, codUnidade, dataInicial, dataFinal);
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
    private PreparedStatement getChecklistRealizadosDia(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "func_relatorio_aderencia_checklist_diaria(?, ?, ?);");
        stmt.setDate(1, dataInicial);
        stmt.setDate(2, dataFinal);
        stmt.setLong(3, codUnidade);
        return stmt;
    }

    @NotNull
    private PreparedStatement getExtratoChecklistRealizadosDia(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT to_char(c.data_hora::date, 'DD/MM/YYYY') as \"DATA\",\n" +
                "c.placa_veiculo AS \"PLACA\",\n" +
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
                "GROUP BY c.data_hora::date, 2\n" +
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
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_MEDIA_TEMPO_REALIZACAO_CHECKLIST(?,?,?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getResumoChecklistStatement(Connection conn, Long codUnidade, Date dataInicial,
                                                          Date dataFinal, String placa)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_checklist_resumo_realizados(?,?,?,?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setString(4, placa);
        return stmt;
    }

    @NotNull
    public Report getResumoChecklistReport(@NotNull Long codUnidade,
                                           @NotNull Date dataInicial,
                                           @NotNull Date dataFinal,
                                           @NotNull String placa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistStatement(conn, codUnidade, dataInicial, dataFinal, placa);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public void getResumoChecklistCsv(@NotNull OutputStream outputStream,
                               @NotNull Long codUnidade,
                               @NotNull Date dataInicial,
                               @NotNull Date dataFinal,
                               @NotNull String placa) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistStatement(conn, codUnidade, dataInicial, dataFinal, placa);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }


    @NotNull
    private PreparedStatement getEstratificacaoRespostasNokChecklistStatement(Connection conn, Long codUnidade, String placa, Date dataInicial,
                                                                              Date dataFinal) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from func_relatorio_checklist_extrato_respostas_nok(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setString(4, placa);
        return stmt;
    }

    @Override
    public void getEstratificacaoRespostasNokChecklistCsv(@NotNull OutputStream outputStream, @NotNull Long codUnidade, @NotNull String placa,
                                                          @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostasNokChecklistStatement(conn, codUnidade, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getEstratificacaoRespostasNokChecklistReport(@NotNull Long codUnidade, @NotNull String placa,
                                                               @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostasNokChecklistStatement(conn, codUnidade, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}