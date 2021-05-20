package test.br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;

import static test.br.com.zalf.prolog.webservice.config.TestConstants.USER_TEST_CPF;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class LiberaTodasPermissoesCallback extends BaseCallback {
    @NotNull
    private static final String TAG = LiberaTodasPermissoesCallback.class.getSimpleName();

    @Override
    public void handle(final Event event, final Context context) {
        if (event.equals(Event.AFTER_MIGRATE)) {
            final String query = "select * from func_libera_todas_permissoes(" + USER_TEST_CPF + ");";
            Log.i(TAG, "Executando query: " + query + " após migrations...");
            try (final Statement statement = context.getConnection().createStatement()) {
                statement.execute(query);
            } catch (final SQLException e) {
                Log.e(TAG, "Erro ao executar callback após migrations", e);
            }
            Log.i(TAG, "Finalizando execução da query: " + query);
        }
    }
}
