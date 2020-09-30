package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogDatabase extends DatabaseConnection {
	private static final String TAG = LogDatabase.class.getSimpleName();

	public static void insertLog(@Nullable final String json,
								 @Nullable final String identificador) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO LOG_JSON(JSON, IDENTIFICADOR) VALUES (?, ?)");
			stmt.setString(1, json);
			stmt.setString(2, identificador);
			stmt.executeUpdate();
		} catch (final SQLException e) {
			Log.e(TAG, "ERRO", e);
		} finally {
			close(conn, stmt);
		}
	}
}
