package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PostgresUtils {

    @Deprecated
    public static Array ListToArray(Connection conn, List<String> list) throws SQLException {
        String[] array = list.toArray(new String[0]);
        return conn.createArrayOf("text", array);
    }

    @Deprecated
    public static Array ListLongToArray(Connection conn, List<Long> list) throws SQLException {
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
}