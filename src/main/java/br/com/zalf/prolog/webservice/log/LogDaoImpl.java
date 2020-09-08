package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.logger._model.LogType;
import br.com.zalf.prolog.webservice.integracao.logger._model.RequestResponseLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

public final class LogDaoImpl extends DatabaseConnection implements LogDao {

    @Override
    public boolean insert(@NotNull final String log, @NotNull final String identificador) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON, IDENTIFICADOR) VALUES (?, ?)");
            stmt.setString(1, log);
            stmt.setString(2, identificador);
            return stmt.executeUpdate() > 0;
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void insertRequestResponseLogApi(@NotNull final String tokenRequisicao,
                                            @NotNull final RequestResponseLog requestLog,
                                            @Nullable final RequestResponseLog responseLog) throws Throwable {
        internalInsertRequestResponseLog(tokenRequisicao, LogType.FROM_API, requestLog, responseLog);
    }

    @Override
    public void insertRequestResponseLogProlog(@NotNull final RequestResponseLog requestLog,
                                               @Nullable final RequestResponseLog responseLog) throws Throwable {
        internalInsertRequestResponseLog(null, LogType.FROM_PROLOG, requestLog, responseLog);
    }

    private void internalInsertRequestResponseLog(@Nullable final String tokenRequisicao,
                                                  @NotNull final LogType logType,
                                                  @NotNull final RequestResponseLog requestLog,
                                                  @Nullable final RequestResponseLog responseLog) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_geral_salva_log_integracao(" +
                    "f_log_type => ?, " +
                    "f_token_integracao => ?, " +
                    "f_response_status => ?, " +
                    "f_request_json => ?, " +
                    "f_response_json => ?, " +
                    "f_data_hora_request => ?);");
            stmt.setString(1, logType.asString());
            bindValueOrNull(stmt, 2, tokenRequisicao, SqlType.TEXT);
            bindValueOrNull(
                    stmt,
                    3,
                    responseLog == null ? null : responseLog.getStatusCode(),
                    SqlType.INTEGER);
            stmt.setObject(4, PostgresUtils.toJsonb(requestLog.toJson()));
            if (responseLog == null) {
                stmt.setNull(5, Types.NULL);
            } else {
                stmt.setObject(5, PostgresUtils.toJsonb(responseLog.toJson()));
            }
            stmt.setObject(6, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}