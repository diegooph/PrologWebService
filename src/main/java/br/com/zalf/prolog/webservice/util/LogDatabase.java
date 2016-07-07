package br.com.zalf.prolog.webservice.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.zalf.prolog.webservice.DatabaseConnection;

public class LogDatabase extends DatabaseConnection{

	public static void insertLog(Object o){

		new Thread(){
			public void run() {
				Connection conn = null;
				PreparedStatement stmt = null;
				try {
					conn = getConnection();
					stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON) VALUES (?)");
					stmt.setString(1, GsonUtils.getGson().toJson(o));
					stmt.executeUpdate();
				}catch(SQLException e){

				}
				finally {
					closeConnection(null, stmt, null);
				}
			};	
		}.start();
	}
}
