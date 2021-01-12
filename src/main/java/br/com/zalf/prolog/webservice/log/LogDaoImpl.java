package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.logger._model.LogType;
import br.com.zalf.prolog.webservice.log._model.RequestLog;
import br.com.zalf.prolog.webservice.log._model.ResponseLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

public final class LogDaoImpl extends DatabaseConnection implements LogDao {

    @Override
    public boolean insert(@NotNull final String log, @NotNull final String identificador) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("insert into log.log_json(json, identificador) values (?, ?)");
            stmt.setString(1, log);
            stmt.setString(2, identificador);
            return stmt.executeUpdate() > 0;
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void insertRequestResponseLog(@NotNull final RequestLog requestLog,
                                         @Nullable final ResponseLog responseLog) throws Throwable {
        internalInsertRequestResponseLog(requestLog, responseLog);
    }

    private void internalInsertRequestResponseLog(@NotNull final RequestLog requestLog,
                                                  @Nullable final ResponseLog responseLog) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from log.func_geral_salva_log(" +
                    "f_log_type => ?, " +
                    "f_token_integracao => ?, " +
                    "f_request_json => ?, " +
                    "f_response_status => ?, " +
                    "f_response_json => ?, " +
                    "f_data_hora_request => ?);");
            stmt.setString(1, LogType.fromApi(requestLog.isFromApi()).asString());
            stmt.setString(2, requestLog.getTokenIntegracao());
            stmt.setObject(3, PostgresUtils.toJsonb(requestLog.toJson()));
            bindValueOrNull(
                    stmt,
                    4,
                    responseLog == null ? null : responseLog.getStatusCode(),
                    SqlType.INTEGER);
            if (responseLog == null) {
                stmt.setNull(5, Types.NULL);
            } else {
                stmt.setObject(5, PostgresUtils.toJsonb(responseLog.toJson()));
            }
            stmt.setObject(6, Now.getOffsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}