package br.com.zalf.prolog.webservice.frota.socorrorota.relatorio;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.StatusSocorroRota;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 12/02/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class SocorroRotaRelatorioDaoImpl implements SocorroRotaRelatorioDao {

    @Override
    public void getDadosGeraisSocorrosRotasCsv(@NotNull final OutputStream out,
                                               @NotNull final List<Long> codUnidades,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               @NotNull final List<String> statusSocorrosRotas)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisSocorrosRotasStmt(conn, codUnidades, dataInicial, dataFinal, statusSocorrosRotas);
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
    public @NotNull Report getDadosGeraisSocorrosRotasReport(@NotNull final List<Long> codUnidades,
                                                             @NotNull final LocalDate dataInicial,
                                                             @NotNull final LocalDate dataFinal,
                                                             @NotNull final List<String> statusSocorrosRotas)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisSocorrosRotasStmt(conn, codUnidades, dataInicial, dataFinal, statusSocorrosRotas);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getDadosGeraisSocorrosRotasStmt(@NotNull final Connection conn,
                                                              @NotNull final List<Long> codUnidades,
                                                              @NotNull final LocalDate dataInicial,
                                                              @NotNull final LocalDate dataFinal,
                                                              @NotNull final List<String> statusSocorrosRotas)
            throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_RELATORIO_DADOS_GERAIS(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        stmt.setArray(4, PostgresUtils.listToArray(conn, SqlType.VARCHAR, statusSocorrosRotas));
        return stmt;
    }

    @Override
    public @NotNull Map<StatusSocorroRota, Integer> getSocorrosPorStatus(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final Map<StatusSocorroRota, Integer> statusSocorroRota = new LinkedHashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_SOCORRO_ROTA_RELATORIO_SOCORROS_POR_STATUS(" +
                    "F_COD_UNIDADES := ?," +
                    "F_DATA_HORA_BUSCA_RELATORIO := ?);");

            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.getLocalDateUtc());

            rSet = stmt.executeQuery();
            while (rSet.next()) {
                statusSocorroRota.put(
                        StatusSocorroRota.fromString(rSet.getString("STATUS")),
                        rSet.getInt("QUANTIDADE_SOCORROS"));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return statusSocorroRota;
    }

}
