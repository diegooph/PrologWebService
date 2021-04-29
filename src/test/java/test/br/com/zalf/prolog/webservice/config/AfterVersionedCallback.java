package test.br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class AfterVersionedCallback extends BaseCallback {
    @NotNull
    private static final String USER_TEST_CPF = "3383283194";
    @NotNull
    private static final String TAG = AfterVersionedCallback.class.getSimpleName();

    @Override
    public void handle(final Event event, final Context context) {
        if (event.equals(Event.AFTER_MIGRATE)) {
            final String query = "select * from func_libera_todas_permissoes(" + USER_TEST_CPF + ");";
            final String query2 = "select setval('pneu_data_codigo_seq', 373770, true);";
            final String query3 = "select setval('pneu_servico_realizado_data_codigo_seq', 183, true);";
            Log.i(TAG, "Executando query: " + query + " após migrations...");
            try (final Statement statement = context.getConnection().createStatement()) {
                statement.execute(query);
                statement.execute(query2);
                statement.execute(query3);
            } catch (final SQLException e) {
                Log.e(TAG, "Erro ao executar callback após migrations", e);
            }
            Log.i(TAG, "Finalizando execução da query: " + query);
        }
    }
}
