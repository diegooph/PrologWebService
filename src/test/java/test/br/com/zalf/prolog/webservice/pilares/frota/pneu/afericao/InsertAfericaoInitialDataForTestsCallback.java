package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class InsertAfericaoInitialDataForTestsCallback extends BaseCallback {

    private static final String TAG = InsertAfericaoInitialDataForTestsCallback.class.getSimpleName();

    @Override
    public void handle(final Event event, final Context context) {
        if (event.equals(Event.AFTER_MIGRATE)) {
            Log.i(TAG, "Iniciando execução da inclusão de dados de aferição de teste...");
            try (final Statement statement = context.getConnection().createStatement()) {
              statement.execute("INSERT INTO public.afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao, tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado, pg_username_delecao, cod_diagrama, forma_coleta_dados, motivo_delecao, cod_veiculo) VALUES (2196, '2019-11-06 23:20:33.701000', 'PRO0001', 3383283194, 21167, 179461, 'SULCO_PRESSAO', 5, 'PLACA', false, null, null, 1, 'EQUIPAMENTO', null, 3195);");
              statement.execute("INSERT INTO public.afericao_data (codigo, data_hora, placa_veiculo, cpf_aferidor, km_veiculo, tempo_realizacao, tipo_medicao_coletada, cod_unidade, tipo_processo_coleta, deletado, data_hora_deletado, pg_username_delecao, cod_diagrama, forma_coleta_dados, motivo_delecao, cod_veiculo) VALUES (10614,'2019-09-19 12:33:06.021000', null, 3383283194, null, 18810, 'SULCO', 5, 'PNEU_AVULSO', false, null, null, null, 'EQUIPAMENTO', null, null);");
          } catch (final SQLException e ) {
              Log.e(TAG, "Erro ao inserir dados de aferição", e);
          }
          Log.i(TAG, "Finalizando execução das inserções");
        }
    }
}
