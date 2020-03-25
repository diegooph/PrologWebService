package br.com.zalf.prolog.webservice.gente.permissao.relatorios;

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
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 25/03/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PermissaoRelatorioDaoImpl implements PermissaoRelatorioDao {
    @Override
    public void getPermissoesDetalhadasCsv(@NotNull final OutputStream out,
                                           @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPermissoesDetalhadasStmt(conn, codUnidades);
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

    @Override
    public Report getPermissoesDetalhadasReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getPermissoesDetalhadasStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getPermissoesDetalhadasStmt(@NotNull final Connection conn,
                                                          @NotNull final List<Long> codUnidades)
            throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_COLABORADOR_RELATORIO_PERMISSOES_DETALHADAS(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }
}
