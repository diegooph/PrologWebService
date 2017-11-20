package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;

/**
 * Classe AppService responsavel por comunicar-se com a interface DAO
 */
public class AppService {

	private AppDao dao = new AppDaoImpl();
	private static final String TAG = AppService.class.getSimpleName();

	public boolean isThisCurrentVersion(AppVersion appVersion) {
		try {
			return dao.isThisCurrentVersion(appVersion);
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao buscar a versão atual do app comparando com a versão: %d", appVersion.getVersionCode()), e);
			// caso erro melhor assumir true para não fazer o usuário ir verificar
			// uma atualização que não existe
			return true;
		}
	}

}
