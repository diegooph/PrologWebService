package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

public class PostgresUtil {

	@Deprecated
	public static Array ListToArray(Connection conn, List<String> list) throws SQLException{
		String[] array = list.toArray(new String[0]);
		return conn.createArrayOf("text", array);
	}

	@Deprecated
	public static Array ListLongToArray(Connection conn, List<Long> list) throws SQLException {
		return conn.createArrayOf("text", list.toArray(new Long[0]));
	}

	@NotNull
	public static Array listLongToArray(@NotNull final Connection conn,
										@NotNull final List<Long> list,
										@NotNull final SqlType type) throws SQLException {
		return conn.createArrayOf(type.asString(), list.toArray(new Long[0]));
	}
}
