package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ServicoRelatorioDaoImpl extends DatabaseConnection implements ServicoRelatorioDao {

    @NotNull
    @Override
    public Report getEstratificacaoServicosFechadosReport(@NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosFechadosStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoServicosFechadosCsv(
            @NotNull final OutputStream outputStream,
            @NotNull final List<Long> codUnidades,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosFechadosStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoServicosAbertosReport(@NotNull final List<Long> codUnidades,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosAbertosStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoServicosAbertosCsv(
            @NotNull final OutputStream outputStream,
            @NotNull final List<Long> codUnidades,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosAbertosStatement(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getEstratificacaoServicosFechadosStatement(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_FECHADOS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoServicosAbertosStatement(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws SQLException {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_ABERTOS(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setObject(4, Now.getLocalDateUtc());
        return stmt;
    }
}