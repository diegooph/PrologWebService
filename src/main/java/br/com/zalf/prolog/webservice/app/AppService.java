package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.commons.login.AppVersion;

import java.sql.SQLException;

/**
 * Classe AppService, responsavel pela comunicação com a camada Dao do pacote app
 */
public class AppService {

	private AppDao dao = new AppDaoImpl();

	/**
	 * método que verifaica se a versão está atualizada
	 * @param appVersion
	 * @return
	 */
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
