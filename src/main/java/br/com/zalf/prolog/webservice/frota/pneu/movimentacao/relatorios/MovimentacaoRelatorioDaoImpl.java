package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.closeConnection;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 04/09/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class MovimentacaoRelatorioDaoImpl implements MovimentacaoRelatorioDao {

    @Override
    public void getDadosGeraisMovimentacoesCsv(@NotNull final OutputStream out,
                                               @NotNull final List<Long> codUnidades,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisMovimentacoesStmt(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getDadosGeraisMovimentacoesReport(@NotNull final List<Long> codUnidades,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisMovimentacoesStmt(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getDadosGeraisMovimentacoesStmt(@NotNull final Connection conn,
                                                              @NotNull final List<Long> codUnidades,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }
}