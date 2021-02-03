package br.com.zalf.prolog.webservice.gente.colaborador.relatorios;

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
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 05/04/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ColaboradorRelatorioDaoImpl implements ColaboradorRelatorioDao {

    @Override
    public void getListagemColaboradoresByUnidadeCsv(@NotNull final OutputStream out,
                                                     @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemColaboradoresByUnidadeStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }

    }

    @NotNull
    @Override
    public Report getListagemColaboradoresByUnidadeReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemColaboradoresByUnidadeStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getListagemColaboradoresByUnidadeStmt(@NotNull final Connection conn,
                                                                    @NotNull final List<Long> codUnidades) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_RELATORIO_LISTAGEM_COLABORADORES_BY_UNIDADE(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }
}
