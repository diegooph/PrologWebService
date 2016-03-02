package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;

import br.com.zalf.prolog.models.AppVersion;
/**
 * Verifica se a versão utilizada é a última disponível.
 */
public interface AppDao {
	boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException;
}
