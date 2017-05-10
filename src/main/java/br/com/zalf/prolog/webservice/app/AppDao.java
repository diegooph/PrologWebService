package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.webservice.commons.login.AppVersion;

import java.sql.SQLException;

public interface AppDao {

	/**
	 * Verifica se a versão utilizada é a última disponível.
	 * @param appVersion versão atual do usuario
	 * @return True se versão atualizada, do contrario, False.
	 * @throws SQLException caso ocorrer erro no banco
	 */
	boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException;
}
