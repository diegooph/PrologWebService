package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.logger.RequestLog;
import br.com.zalf.prolog.webservice.integracao.logger.ResponseLog;
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
    public void insertRequestResponseLog(@NotNull final String tokenRequisicao,
                                         @NotNull final RequestLog requestLog,
                                         @Nullable final ResponseLog responseLog) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_GERAL_SALVA_LOG_INTEGRACAO(" +
                    "F_TOKEN_INTEGRACAO := ?, " +
                    "F_RESPONSE_STATUS := ?, " +
                    "F_REQUEST_JSON := ?, " +
                    "F_RESPONSE_JSON := ?," +
                    "F_DATA_HORA_REQUEST := ?)");
            stmt.setString(1, tokenRequisicao);
            bindValueOrNull(
                    stmt,
                    2,
                    responseLog == null ? null : responseLog.getStatusCode(),
                    SqlType.INTEGER);
            stmt.setObject(3, PostgresUtils.toJsonb(RequestLog.toJson(requestLog)));
            if (responseLog == null) {
                stmt.setNull(4, Types.NULL);
            } else {
                stmt.setObject(4, PostgresUtils.toJsonb(ResponseLog.toJson(responseLog)));
            }
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}