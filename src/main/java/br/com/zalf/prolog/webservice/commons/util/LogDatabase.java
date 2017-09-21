package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogDatabase extends DatabaseConnection{
	
	private static final String TAG = LogDatabase.class.getSimpleName();

	public static void insertLog(Object o){

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON) VALUES (?)");
			String json = GsonUtils.getGson().toJson(o);
			stmt.setString(1, json);
			stmt.executeUpdate();
		}catch(SQLException e){
			Log.e(TAG, "ERRO", e);
		}
		finally {
			closeConnection(conn, stmt, null);
		}
	}
	
	public static void insertLog(String json){

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON) VALUES (?)");
			stmt.setString(1, json);
			stmt.executeUpdate();
		}catch(SQLException e){
			Log.e(TAG, "ERRO", e);
		}
		finally {
			closeConnection(conn, stmt, null);
		}
	}
}
