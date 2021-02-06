package br.com.zalf.prolog.webservice.app;

import java.sql.SQLException;

public interface AppDao {

	boolean isThisCurrentVersion(AppVersion appVersion) throws SQLException;
}