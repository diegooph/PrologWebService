package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.commons.login.AppVersion;

import java.sql.SQLException;

public class AppService {
	private AppDaoImpl dao = new AppDaoImpl();
	
	public boolean isThisCurrentVersion(AppVersion appVersion) {
		try {
			return dao.isThisCurrentVersion(appVersion);
		} catch (SQLException e) {
			e.printStackTrace();
			// caso erro melhor assumir true para não fazer o usuário ir verificar
			// uma atualização que não existe
			return true;
		}
	}

}
