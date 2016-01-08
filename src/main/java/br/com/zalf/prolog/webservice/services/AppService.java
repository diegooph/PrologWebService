package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;

import br.com.empresa.oprojeto.models.AppVersion;
import br.com.zalf.prolog.webservice.dao.AppDaoImpl;

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
