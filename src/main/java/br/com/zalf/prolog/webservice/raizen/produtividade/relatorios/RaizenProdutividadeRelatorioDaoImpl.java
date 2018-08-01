package br.com.zalf.prolog.webservice.raizen.produtividade.relatorios;

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
 * Created on 31/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeRelatorioDaoImpl extends DatabaseConnection implements RaizenProdutividadeRelatorioDao {

    @Override
    public void getDadosGeraisProdutividadeCsv(@NotNull final OutputStream out,
                                               @NotNull final Long codEmpresa,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisProdutividadeStmt(conn, codEmpresa, dataInicial, dataFinal);
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
    public Report getDadosGeraisProdutividadeReport(@NotNull final Long codEmpresa,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisProdutividadeStmt(conn, codEmpresa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getDadosGeraisProdutividadeStmt(@NotNull final Connection conn,
                                                              @NotNull final Long codEmpresa,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM " +
                        "FUNC_RAIZEN_PRODUTIVIDADE_RELATORIO_DADOS_GERAIS_PRODUTIVIDADE(?, ?, ?);");
        stmt.setLong(1, codEmpresa);
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }
}