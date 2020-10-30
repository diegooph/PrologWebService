package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.log._model.RequestLog;
import br.com.zalf.prolog.webservice.log._model.ResponseLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe LogService responsavel por comunicar-se com a interface DAO
 */
public final class LogService {
	@NotNull
	private static final String TAG = LogService.class.getSimpleName();
	@NotNull
	private final LogDao dao = Injection.provideLogDao();

	public boolean insert(@NotNull final String log, @NotNull final String identificador) {
		try {
			return dao.insert(log, identificador);
		} catch (final SQLException e) {
			// Não passamos o log como parâmetro para a exceção pois ele tem tamanho desconhecido. Podendo ser um JSON
			// grande.
			Log.e(TAG, "Erro ao inserir log com identificador: " + identificador, e);
			throw new RuntimeException(e);
		}
	}

	public void saveLogToDatabaseAsync(@NotNull final RequestLog requestLog,
									   @Nullable final ResponseLog responseLog) {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			try {
				dao.insertRequestResponseLog(requestLog, responseLog);
			} catch (final Throwable t) {
				Log.e(TAG, "Erro ao inserir log de requisição no banco de dados", t);
			}
		});
		executor.shutdown();
	}
}
