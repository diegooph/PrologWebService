package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ServicoRelatorioDaoImpl extends DatabaseConnection implements ServicoRelatorioDao {

    @NotNull
    @Override
    public Report getEstratificacaoServicosFechadosReport(@NotNull final Long codUnidade,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosFechadosStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoServicosFechadosCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosFechadosStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoServicosAbertosReport(@NotNull final Long codUnidade,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosAbertosStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoServicosAbertosCsv(@NotNull final OutputStream outputStream,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws IOException, SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoServicosAbertosStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getEstratificacaoServicosFechadosStatement(@NotNull final Connection conn,
                                                                         @NotNull final Long codUnidade,
                                                                         @NotNull final LocalDate dataInicial,
                                                                         @NotNull final LocalDate dataFinal)
            throws SQLException {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS(?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    private PreparedStatement getEstratificacaoServicosAbertosStatement(@NotNull final Connection conn,
                                                                         @NotNull final Long codUnidade,
                                                                         @NotNull final LocalDate dataInicial,
                                                                         @NotNull final LocalDate dataFinal)
            throws SQLException {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(?, ?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setObject(4, Now.localDate());
        stmt.setString(5, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
        return stmt;
    }
}