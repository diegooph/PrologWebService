package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.errorhandling.sql.SqlErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;
import org.postgresql.util.PSQLException;

import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PostgresUtils {
    @Deprecated
    public static Array ListToArray(final Connection conn, final List<String> list) throws SQLException {
        final String[] array = list.toArray(new String[0]);
        return conn.createArrayOf("text", array);
    }

    @Deprecated
    public static Array ListLongToArray(final Connection conn, final List<Long> list) throws SQLException {
        return conn.createArrayOf("text", list.toArray(new Long[0]));
    }

    @NotNull
    public static Array listToArray(@NotNull final Connection conn,
                                    @NotNull final SqlType type,
                                    @NotNull final List<?> list) throws SQLException {
        return conn.createArrayOf(type.asString(), list.toArray());
    }

    @NotNull
    public static PGobject toJsonb(@NotNull final String json) throws SQLException {
        final PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(json);
        return pgObject;
    }

    @NotNull
    public static String getPSQLErrorMessage(@NotNull final SQLException sqlException,
                                             @NotNull final String fallbackMessage) {
        if (String.valueOf(sqlException.getSQLState()).equals(SqlErrorCodes.BD_GENERIC_ERROR_CODE.getErrorCode())) {
            if (sqlException instanceof PSQLException) {
                return ((PSQLException) sqlException).getServerErrorMessage().getMessage();
            } else if (sqlException instanceof BatchUpdateException) {
                if (sqlException.getNextException() instanceof PSQLException) {
                    return ((PSQLException) sqlException.getNextException()).getServerErrorMessage().getMessage();
                }
            }
        }
        return fallbackMessage;
    }
}