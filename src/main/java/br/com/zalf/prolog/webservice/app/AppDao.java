package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.commons.login.AppVersion;

import java.sql.SQLException;

public interface AppDao {

	/**
	 * Verifica se a versão utilizada é a última disponível.
	 * @param appVersion
	 * @return True se versão atualizada, do contrario, False.
	 * @throws SQLException
	 */
	boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException;
}
