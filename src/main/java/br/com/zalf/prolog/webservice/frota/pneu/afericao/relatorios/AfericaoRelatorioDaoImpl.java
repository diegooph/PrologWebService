package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.closeConnection;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 30/08/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class AfericaoRelatorioDaoImpl implements AfericaoRelatorioDao {


    @Override
    public void getDadosGeraisAfericaoCsv(@NotNull final OutputStream out,
                                          @NotNull final Long codUnidade,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisAfericaoStmt(conn, codUnidade, dataInicial, dataFinal);
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
    public Report getDadosGeraisAfericaoReport(@NotNull final Long codUnidade,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisAfericaoStmt(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getDadosGeraisAfericaoStmt(@NotNull final Connection conn,
                                                         @NotNull final Long codUnidade,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(?,?,?);");
        final List<Long> codUnidades = new ArrayList<>();
        codUnidades.add(codUnidade);
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }
}