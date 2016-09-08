package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.commons.login.AppVersion;

import java.sql.SQLException;

/**
 * Verifica se a versão utilizada é a última disponível.
 */
public interface AppDao {
	boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException;
}
