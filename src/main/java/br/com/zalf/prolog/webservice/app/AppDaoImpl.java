package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe AppDapImpl, responsavel pela execução da lógica
 * e comunicação com a camada de dados da aplicação
 */
public class AppDaoImpl extends DatabaseConnection implements AppDao {

	public AppDaoImpl() {

	}

	@Override
	public boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			// Precisa do "AS VERSION_CODE" por que senão retorna com o nome MAX
			stmt = conn.prepareStatement("SELECT MAX(VERSION_CODE) "
					+ "AS VERSION_CODE FROM APP_VERSION");
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				int versionCode = rSet.getInt("VERSION_CODE");
				if (versionCode == appVersion.getVersionCode()) {
					return true;
				} else {
					return false;
				}
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}

}
