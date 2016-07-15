package br.com.zalf.prolog.webservice.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.zalf.prolog.webservice.DatabaseConnection;

public class LogDaoImpl extends DatabaseConnection {

	public boolean insert(String log, String indicador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON, IDENTIFICADOR) VALUES (?,?)");
			stmt.setString(1, log);
			stmt.setString(2, indicador);
			int count = stmt.executeUpdate();
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

}
