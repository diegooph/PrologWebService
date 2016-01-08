package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;

import br.com.empresa.oprojeto.models.AppVersion;

public interface AppDao {
	boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException;
}
