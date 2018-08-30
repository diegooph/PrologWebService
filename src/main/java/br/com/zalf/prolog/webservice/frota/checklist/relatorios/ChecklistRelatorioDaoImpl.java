package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Created by luiz on 25/04/17.
 */
public class ChecklistRelatorioDaoImpl extends DatabaseConnection implements ChecklistRelatorioDao {

    public ChecklistRelatorioDaoImpl() {

    }

    @Override
    public void getChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final Long codUnidade,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable {
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

    @NotNull
    @Override
    public Report getChecklistsRealizadosDiaReport(@NotNull final Long codUnidade,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable {
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
    public void getExtratoChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal) throws Throwable {
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

    @NotNull
    @Override
    public Report getExtratoChecklistsRealizadosDiaReport(@NotNull final Long codUnidade,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
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
    public void getTempoRealizacaoChecklistMotoristaCsv(@NotNull final OutputStream outputStream,
                                                        @NotNull final Long codUnidade,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
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

    @NotNull
    @Override
    public Report getTempoRealizacaoChecklistMotoristaReport(@NotNull final Long codUnidade,
                                                             @NotNull final LocalDate dataInicial,
                                                             @NotNull final LocalDate dataFinal) throws Throwable {
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
    public Report getResumoChecklistReport(@NotNull final Long codUnidade,
                                           @NotNull final String placa,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistStatement(conn, codUnidade, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public void getResumoChecklistCsv(@NotNull final OutputStream outputStream,
                                      @NotNull final Long codUnidade,
                                      @NotNull final String placa,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistStatement(conn, codUnidade, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }


    @Override
    public void getEstratificacaoRespostasNokChecklistCsv(@NotNull final OutputStream outputStream,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final String placa,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
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

    @NotNull
    @Override
    public Report getEstratificacaoRespostasNokChecklistReport(@NotNull final Long codUnidade,
                                                               @NotNull final String placa,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws Throwable {
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

    @NotNull
    private PreparedStatement getExtratoChecklistRealizadosDia(@NotNull final Connection conn,
                                                               @NotNull final Long codUnidade,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT to_char((c.data_hora AT TIME ZONE ?)::LocalDate, 'DD/MM/YYYY') as \"DATA\", " +
                "c.placa_veiculo AS \"PLACA\"," +
                "sum(case when c.tipo = 'S' then 1 else 0 end) as \"CHECKS SAÍDA\", " +
                "sum(case when c.tipo = 'R' then 1 else 0 end) as \"CHECKS RETORNO\" " +
                "FROM checklist c " +
                "LEFT JOIN " +
                "(SELECT m.data as data_mapa, m.mapa, m.placa " +
                "FROM mapa m " +
                "JOIN veiculo v on v.placa = m.placa " +
                "WHERE m.cod_unidade = ? and m.data BETWEEN ? and ? " +
                "ORDER BY m.data asc) as dia_mapas ON dia_mapas.data_mapa = c.data_hora::LocalDate and dia_mapas.placa = c.placa_veiculo " +
                "WHERE c.cod_unidade = ? and c.data_hora::LocalDate BETWEEN ? and ? " +
                "GROUP BY c.data_hora, 2 " +
                "ORDER BY c.data_hora::LocalDate;");
        stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
        stmt.setLong(2, codUnidade);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        stmt.setLong(5, codUnidade);
        stmt.setObject(6, dataInicial);
        stmt.setObject(7, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getChecklistRealizadosDia(@NotNull final Connection conn,
                                                        @NotNull final Long codUnidade,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "func_relatorio_aderencia_checklist_diaria(?, ?, ?);");
        stmt.setObject(1, dataInicial);
        stmt.setObject(2, dataFinal);
        stmt.setLong(3, codUnidade);
        return stmt;
    }

    @NotNull
    private PreparedStatement getTempoRealizacaoChecklistMotorista(@NotNull final Connection conn,
                                                                   @NotNull final Long codUnidade,
                                                                   @NotNull final LocalDate dataInicial,
                                                                   @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_MEDIA_TEMPO_REALIZACAO_CHECKLIST(?,?,?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getResumoChecklistStatement(@NotNull final Connection conn,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final String placa,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_checklist_resumo_realizados(?,?,?,?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setString(4, placa);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoRespostasNokChecklistStatement(@NotNull final Connection conn,
                                                                              @NotNull final Long codUnidade,
                                                                              @NotNull final String placa,
                                                                              @NotNull final LocalDate dataInicial,
                                                                              @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("select * from func_relatorio_checklist_extrato_respostas_nok(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setString(4, placa);
        return stmt;
    }
}