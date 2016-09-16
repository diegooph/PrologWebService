package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.commons.login.AppVersion;

import java.sql.SQLException;

/**
 * Classe AppService responsavel por comunicar-se com a interface DAO
 */
public class AppService {

	private AppDao dao = new AppDaoImpl();

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
