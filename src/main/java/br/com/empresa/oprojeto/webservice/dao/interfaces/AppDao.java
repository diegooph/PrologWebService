package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;

import br.com.empresa.oprojeto.models.AppVersion;

public interface AppDao {
	boolean isCurrentVersion(AppVersion appVersion) throws SQLException;
}
