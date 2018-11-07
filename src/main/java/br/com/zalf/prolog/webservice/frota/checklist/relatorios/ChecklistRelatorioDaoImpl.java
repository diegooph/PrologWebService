package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.QuantidadeChecklists;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
public class ChecklistRelatorioDaoImpl extends DatabaseConnection implements ChecklistRelatorioDao {

    public ChecklistRelatorioDaoImpl() {

    }

    @NotNull
    @Override
    public List<QuantidadeChecklists> getQtdChecklistsRealizadosByTipo(@NotNull final List<Long> codUnidades,
                                                                       final int diasRetroativosParaBuscar)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_RELATORIO_QTD_POR_TIPO(?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.localDateUtc());
            stmt.setInt(3, diasRetroativosParaBuscar);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<QuantidadeChecklists> checklists = new ArrayList<>();
                do {
                    checklists.add(new QuantidadeChecklists(
                            rSet.getObject("DATA", LocalDate.class),
                            rSet.getString("DATA_FORMATADA"),
                            rSet.getInt("TOTAL_CHECKLISTS_SAIDA"),
                            rSet.getInt("TOTAL_CHECKLISTS_RETORNO")));
                } while (rSet.next());
                return checklists;
            } else {
                throw new IllegalStateException("Erro ao buscar as informações de checklists realizados para as " +
                        "unidades: " + codUnidades);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final List<Long> codUnidades,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getChecklistRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getChecklistRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getExtratoChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                                          @NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoChecklistsRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getExtratoChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidades,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoChecklistsRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getTempoRealizacaoChecklistsMotoristasCsv(@NotNull final OutputStream outputStream,
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
    public Report getTempoRealizacaoChecklistsMotoristasReport(@NotNull final List<Long> codUnidades,
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

    @Override
    public void getListagemModelosChecklistCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemModelosChecklistStatement(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getListagemModelosChecklistReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemModelosChecklistStatement(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getDadosGeraisChecklistCsv(@NotNull final OutputStream outputStream,
                                           @NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal,
                                           @NotNull final String nomeColaborador,
                                           @NotNull final String placa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisChecklistStatement(conn, codUnidades, dataInicial, dataFinal, nomeColaborador, placa);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

    }

    @NotNull
    @Override
    public Report getDadosGeraisChecklistReport(@NotNull final List<Long> codUnidades,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal,
                                                @NotNull final String nomeColaborador,
                                                @NotNull final String placa) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisChecklistStatement(conn, codUnidades, dataInicial, dataFinal, nomeColaborador, placa);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getDadosGeraisChecklistStatement(@NotNull final Connection conn,
                                                               @NotNull final List<Long> codUnidades,
                                                               @NotNull final String dataInicial,
                                                               @NotNull final String dataFinal,
                                                               @NotNull final String nomeColaborador,
                                                               @NotNull final String placa)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_DADOS_GERAIS(?,?,?,?,?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setString(4, nomeColaborador);
        stmt.setString(5, placa);

        return stmt;
    }


    private PreparedStatement getListagemModelosChecklistStatement(@NotNull final Connection conn,
                                                                   @NotNull final List<Long> codUnidades)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getExtratoChecklistsRealizadosDiaAmbev(@NotNull final Connection conn,
                                                                     @NotNull final List<Long> codUnidades,
                                                                     @NotNull final LocalDate dataInicial,
                                                                     @NotNull final LocalDate dataFinal)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getChecklistRealizadosDiaAmbev(@NotNull final Connection conn,
                                                             @NotNull final List<Long> codUnidades,
                                                             @NotNull final LocalDate dataInicial,
                                                             @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getTempoRealizacaoChecklistsMotorista(@NotNull final Connection conn,
                                                                    @NotNull final List<Long> codUnidades,
                                                                    @NotNull final LocalDate dataInicial,
                                                                    @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS(?, ?, ?);");
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
                "FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, placa);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
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
        stmt.setString(2, placa);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }
}