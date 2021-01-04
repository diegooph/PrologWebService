package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;

public class AppService {
	private static final String TAG = AppService.class.getSimpleName();
	private final AppDao dao = Injection.provideAppDao();

	public boolean isThisCurrentVersion(AppVersion appVersion) {
		try {
			return dao.isThisCurrentVersion(appVersion);
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao buscar a versão atual do app comparando com a versão: %d", appVersion.getVersionCode()), e);
			// Em caso de erro melhor assumir true para não fazer o usuário ir verificar
			// uma atualização que não existe
			return true;
		}
	}

}
