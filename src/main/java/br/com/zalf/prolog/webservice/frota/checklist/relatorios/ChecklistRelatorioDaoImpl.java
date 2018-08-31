package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
public class ChecklistRelatorioDaoImpl extends DatabaseConnection implements ChecklistRelatorioDao {

    public ChecklistRelatorioDaoImpl() {

    }

    @Override
    public void getChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final List<Long> codUnidades,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getChecklistRealizadosDia(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getChecklistsRealizadosDiaReport(@NotNull final List<Long> codUnidades,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getChecklistRealizadosDia(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getExtratoChecklistsRealizadosDiaCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final List<Long> codUnidades,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoChecklistRealizadosDia(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getExtratoChecklistsRealizadosDiaReport(@NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoChecklistRealizadosDia(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getTempoRealizacaoChecklistsMotoristaCsv(@NotNull final OutputStream outputStream,
                                                         @NotNull final List<Long> codUnidades,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTempoRealizacaoChecklistsMotorista(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getTempoRealizacaoChecklistsMotoristaReport(@NotNull final List<Long> codUnidades,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTempoRealizacaoChecklistsMotorista(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    public Report getResumoChecklistsReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String placa,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistsStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public void getResumoChecklistsCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String placa,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistsStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }


    @Override
    public void getEstratificacaoRespostasNokCsv(@NotNull final OutputStream outputStream,
                                                 @NotNull final List<Long> codUnidades,
                                                 @NotNull final String placa,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostasNokStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoRespostasNokReport(@NotNull final List<Long> codUnidades,
                                                      @NotNull final String placa,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostasNokStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getExtratoChecklistRealizadosDia(@NotNull final Connection conn,
                                                               @NotNull final List<Long> codUnidades,
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
        // TODO:
//        stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidades, conn).getId());
        stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        stmt.setArray(5, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(6, dataInicial);
        stmt.setObject(7, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getChecklistRealizadosDia(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_CHECKLISTS_REALIZADOS_DIA(?, ?, ?);");
        stmt.setObject(1, dataInicial);
        stmt.setObject(2, dataFinal);
        stmt.setArray(3, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getTempoRealizacaoChecklistsMotorista(@NotNull final Connection conn,
                                                                    @NotNull final List<Long> codUnidades,
                                                                    @NotNull final LocalDate dataInicial,
                                                                    @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_CHECKLISTS_MOTORISTAS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getResumoChecklistsStatement(@NotNull final Connection conn,
                                                           @NotNull final List<Long> codUnidades,
                                                           @NotNull final String placa,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_RESUMO_CHECKLISTS_REALIZADOS(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setString(4, placa);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoRespostasNokStatement(@NotNull final Connection conn,
                                                                     @NotNull final List<Long> codUnidades,
                                                                     @NotNull final String placa,
                                                                     @NotNull final LocalDate dataInicial,
                                                                     @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setString(4, placa);
        return stmt;
    }
}