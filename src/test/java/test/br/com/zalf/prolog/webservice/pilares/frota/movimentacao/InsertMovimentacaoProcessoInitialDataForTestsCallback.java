package test.br.com.zalf.prolog.webservice.pilares.frota.movimentacao;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 2021-04-28
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class InsertMovimentacaoProcessoInitialDataForTestsCallback extends BaseCallback {

    private static final String TAG = InsertMovimentacaoProcessoInitialDataForTestsCallback.class.getSimpleName();

    @Override
    public void handle(final Event event, final Context context) {
        if (event.equals(Event.AFTER_MIGRATE)) {
            Log.i(TAG, "Iniciando execução da inclusão de dados de movimentação de teste...");
            try (final Statement statement = context.getConnection().createStatement()) {
                statement.execute(
                        "INSERT INTO public.pneu (codigo_cliente, cod_modelo, cod_dimensao, pressao_recomendada, "
                                + "pressao_atual, altura_sulco_interno, altura_sulco_central_interno, "
                                + "altura_sulco_externo, cod_unidade, status, vida_atual, vida_total, cod_modelo_banda,"
                                + "altura_sulco_central_externo, dot, valor, data_hora_cadastro, "
                                + "pneu_novo_nunca_rodado, codigo, "
                                + "cod_empresa, cod_unidade_cadastro, origem_cadastro) "
                                + "VALUES ('ab12311', 152, 22, 100, 0, null, null, null, 215, 'EM_USO', 2, 2, 780, "
                                + "null, '1118', 1000, '2020-01-29 11:22:05.357265', false, 381674, 3, 215, "
                                + "'PROLOG_WEB');");
                statement.execute("INSERT INTO public.movimentacao_processo (codigo, cod_unidade, data_hora, "
                                          + "cpf_responsavel, observacao) "
                                          + "VALUES(58889, 215, '2020-01-29 12:34:55.331000', 3383283194, '');");
                statement.execute("INSERT INTO public.movimentacao (codigo, cod_movimentacao_processo, cod_unidade, "
                                          + "cod_pneu, sulco_interno, sulco_central_interno, sulco_externo, vida, "
                                          + "observacao, sulco_central_externo, pressao_atual)"
                                          + "VALUES (236609, 58889, 215, 381674, null, null, null, 2, '', null, null)");
                statement.execute("INSERT INTO public.movimentacao_origem (cod_movimentacao, tipo_origem, "
                                          + "km_veiculo, posicao_pneu_origem, cod_diagrama, cod_veiculo) "
                                          + "VALUES (236609, 'ESTOQUE', null, null, null, null);");
                statement.execute(
                        "INSERT INTO public.movimentacao_destino (cod_movimentacao, tipo_destino, km_veiculo, "
                                + "posicao_pneu_destino, cod_motivo_descarte, url_imagem_descarte_1, "
                                + "url_imagem_descarte_2, url_imagem_descarte_3, cod_recapadora_destino, cod_coleta, "
                                + "cod_diagrama, cod_veiculo) VALUES (236609, 'EM_USO', 119312, 222, null, null, null, "
                                + "null, null, null, 9, 23364);");
            } catch (final SQLException e) {
                Log.e(TAG, "Erro ao inserir dados de movimentação", e);
            }
            Log.i(TAG, "Finalizando execução das inserções de movimentação");
        }
    }
}
