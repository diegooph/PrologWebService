package br.com.zalf.prolog.webservice.util;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PostgresUtil {
	
	
	public static Array ListToArray(Connection conn, List<String> list) throws SQLException{
		String[] array = (String[]) list.toArray(new String[list.size()]);
		return conn.createArrayOf("text", array);
	}

}
