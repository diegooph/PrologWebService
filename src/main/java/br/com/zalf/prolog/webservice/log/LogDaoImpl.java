package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.logger._model.*;
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
                                            @NotNull final RequestLogApi requestLog,
                                            @Nullable final ResponseLogApi responseLog) throws Throwable {
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
            stmt.setString(1, LogType.FROM_API.asString());
            stmt.setString(2, tokenRequisicao);
            bindValueOrNull(
                    stmt,
                    3,
                    responseLog == null ? null : responseLog.getStatusCode(),
                    SqlType.INTEGER);
            stmt.setObject(4, PostgresUtils.toJsonb(RequestLogApi.toJson(requestLog)));
            if (responseLog == null) {
                stmt.setNull(5, Types.NULL);
            } else {
                stmt.setObject(5, PostgresUtils.toJsonb(ResponseLogApi.toJson(responseLog)));
            }
            stmt.setObject(6, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void insertRequestResponseLogProlog(@NotNull final RequestLogProlog requestLog,
                                               @Nullable final ResponseLogProlog responseLog) throws Throwable {
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
            stmt.setString(1, LogType.FROM_PROLOG.asString());
            stmt.setNull(2, Types.NULL);
            bindValueOrNull(
                    stmt,
                    3,
                    responseLog == null ? null : responseLog.getStatusCode(),
                    SqlType.INTEGER);
            stmt.setObject(4, PostgresUtils.toJsonb(RequestLogProlog.toJson(requestLog)));
            if (responseLog == null) {
                stmt.setNull(5, Types.NULL);
            } else {
                stmt.setObject(5, PostgresUtils.toJsonb(ResponseLogProlog.toJson(responseLog)));
            }
            stmt.setObject(6, Now.offsetDateTimeUtc());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}