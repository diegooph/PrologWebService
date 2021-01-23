package br.com.zalf.prolog.webservice.gente.quiz.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Created by Zart on 20/03/17.
 */
public class QuizRelatorioDaoImpl extends DatabaseConnection implements QuizRelatorioDao {

    @Override
    public void getEstratificacaoRealizacaoQuizCsv(@NotNull final OutputStream out,
                                                   @NotNull final Long codUnidade,
                                                   @Nullable final Long codModeloQuiz,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRealizacaoQuiz(conn, codUnidade, codModeloQuiz, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoRealizacaoQuizReport(@NotNull final Long codUnidade,
                                                        @Nullable final Long codModeloQuiz,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRealizacaoQuiz(conn, codUnidade, codModeloQuiz, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getRealizacaoQuizByCargoCsv(@NotNull final OutputStream out,
                                            @NotNull final Long codUnidade,
                                            @Nullable final Long codModeloQuiz) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRealizacaoQuizByCargo(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getRealizacaoQuizByCargoReport(@NotNull final Long codUnidade,
                                                 @Nullable final Long codModeloQuiz) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRealizacaoQuizByCargo(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoQuizRespostasCsv(@NotNull final OutputStream out,
                                                  @NotNull final Long codUnidade,
                                                  @Nullable final Long codModeloQuiz)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostas(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoQuizRespostasReport(@NotNull final Long codUnidade,
                                                       @Nullable final Long codModeloQuiz) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostas(conn, codUnidade, codModeloQuiz);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getExtratoGeralCsv(@NotNull final OutputStream out,
                                   @NotNull final Long codUnidade,
                                   @NotNull final LocalDate dataInicial,
                                   @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoGeral(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getExtratoGeralReport(@NotNull final Long codUnidade,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoGeral(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getRespostasRealizadosCsv(@NotNull final OutputStream out,
                                          @NotNull final Long codUnidade,
                                          @Nullable final Long codModeloQuiz,
                                          @Nullable final Long cpfColaborador,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal,
                                          final boolean apenasSelecionadas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRespostasRealizadosStmt(conn,
                    codUnidade,
                    codModeloQuiz,
                    cpfColaborador,
                    dataInicial,
                    dataFinal,
                    apenasSelecionadas);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getRespostasRealizadosReport(@NotNull final Long codUnidade,
                                               @Nullable final Long codModeloQuiz,
                                               @Nullable final Long cpfColaborador,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final boolean apenasSelecionadas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRespostasRealizadosStmt(conn,
                    codUnidade,
                    codModeloQuiz,
                    cpfColaborador,
                    dataInicial,
                    dataFinal,
                    apenasSelecionadas);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    /*
     * ========================================================
     * ========================================================
     * ========================================================
     * PRIVATE METHODS
     * ========================================================
     * ========================================================
     * ========================================================
     */
    @NotNull
    private PreparedStatement getRealizacaoQuizByCargo(@NotNull final Connection conn,
                                                       @NotNull final Long codUnidade,
                                                       @Nullable final Long codModeloQuiz) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_QUIZ_RELATORIO_REALIZACAO_CARGO(" +
                "F_COD_UNIDADE := ?," +
                "F_COD_MODELO  := ?);");
        stmt.setLong(1, codUnidade);
        StatementUtils.bindValueOrNull(stmt, 2, codModeloQuiz, SqlType.BIGINT);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoRespostas(@NotNull final Connection conn,
                                                         @NotNull final Long codUnidade,
                                                         @Nullable final Long codModeloQuiz) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_QUIZ_RELATORIO_ESTRATIFICACAO_RESPOSTAS(" +
                "F_COD_UNIDADE := ?," +
                "F_COD_MODELO  := ?);");
        stmt.setLong(1, codUnidade);
        StatementUtils.bindValueOrNull(stmt, 2, codModeloQuiz, SqlType.BIGINT);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoRealizacaoQuiz(@NotNull final Connection conn,
                                                              @NotNull final Long codUnidade,
                                                              @Nullable final Long codModeloQuiz,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_QUIZ_RELATORIO_ESTRATIFICACAO_REALIZACAO(" +
                "F_COD_UNIDADE  := ?," +
                "F_COD_MODELO   := ?," +
                "F_DATA_INICIAL := ?," +
                "F_DATA_FINAL   := ?);");
        stmt.setLong(1, codUnidade);
        StatementUtils.bindValueOrNull(stmt, 2, codModeloQuiz, SqlType.BIGINT);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getExtratoGeral(@NotNull final Connection conn,
                                              @NotNull final Long codUnidade,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_QUIZ_RELATORIO_EXTRATO_GERAL(" +
                "F_COD_UNIDADE  := ?," +
                "F_DATA_INICIAL := ?," +
                "F_DATA_FINAL   := ?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getRespostasRealizadosStmt(@NotNull final Connection conn,
                                                         @NotNull final Long codUnidade,
                                                         @Nullable final Long codModelo,
                                                         @Nullable final Long cpfColaborador,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal,
                                                         final boolean apenasSelecionadas) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_QUIZ_RELATORIO_RESPOSTAS_REALIZADOS(" +
                "F_COD_UNIDADE         := ?," +
                "F_COD_MODELO          := ?," +
                "F_CPF                 := ?," +
                "F_DATA_INICIAL        := ?," +
                "F_DATA_FINAL          := ?," +
                "F_APENAS_SELECIONADAS := ?)");
        stmt.setLong(1, codUnidade);
        StatementUtils.bindValueOrNull(stmt, 2, codModelo, SqlType.BIGINT);
        StatementUtils.bindValueOrNull(stmt, 3, cpfColaborador, SqlType.BIGINT);
        stmt.setObject(4, dataInicial);
        stmt.setObject(5, dataFinal);
        stmt.setBoolean(6, apenasSelecionadas);
        return stmt;
    }
}
