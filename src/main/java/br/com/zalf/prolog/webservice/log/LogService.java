package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;

/**
 * Classe LogService responsavel por comunicar-se com a interface DAO
 */
public class LogService {
	private static final String TAG = LogService.class.getSimpleName();
	private final LogDao dao = Injection.provideLogDao();

	public boolean insert(String log, String identificador) {
		try {
			return dao.insert(log, identificador);
		} catch (SQLException e) {
			// Não passamos o log como parâmetro para a excepção pois ele tem tamanho desconhecido. Podendo ser um JSON
			// grande.
			Log.e(TAG, "Erro ao inserir log com identificador: " + identificador, e);
			throw new RuntimeException(e);
		}
	}

}
