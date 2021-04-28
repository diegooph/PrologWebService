package test.br.com.zalf.prolog.webservice.pilares.frota.checklist;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 2021-04-27
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class InsertChecklistInitialDataForTestsCallback extends BaseCallback {

    private static final String TAG = InsertChecklistInitialDataForTestsCallback.class.getSimpleName();
    private static final int QTD_CHECKLISTS = 10;

    @Override
    public void handle(final Event event, final Context context) {
        if (event.equals(Event.AFTER_MIGRATE)) {
            Log.i(TAG, "Iniciando execução da inclusão do veículo para testes de checklist...");
            try (final Statement stmt = context.getConnection().createStatement()) {
                final ResultSet rSetCodVeiculo = insertVeiculoChecklist(stmt);
                if (rSetCodVeiculo.next()) {
                    final Long codVeiculo = rSetCodVeiculo.getLong("codigo");
                    rSetCodVeiculo.next();
                    int count = 0;
                    do {
                        Log.i(TAG, "Iniciando execução da inclusão de dados de checklist de teste...");
                        final ResultSet rSetCodChecklist = insertChecklist(stmt, codVeiculo);
                        if (rSetCodChecklist.next()) {
                            Log.i(TAG, "Iniciando execução da inclusão das respostas de checklist de teste...");
                            final Long codChecklist = rSetCodChecklist.getLong("codigo");
                            insertRespostasChecklist(stmt, codChecklist);
                        }
                        count++;
                        rSetCodChecklist.close();
                    } while (count < QTD_CHECKLISTS);
                }
            } catch (final SQLException e) {
                Log.e(TAG, "Erro ao inserir dados de checklist", e);
            }
            Log.i(TAG, "Execução das inserções finalizada");
        }
    }

    private ResultSet insertVeiculoChecklist(final Statement stmt) throws SQLException {
        return stmt.executeQuery(
                "INSERT INTO public.veiculo_data (placa, cod_unidade, km, status_ativo, cod_tipo, cod_modelo," +
                        " cod_eixos, data_hora_cadastro, cod_unidade_cadastro, deletado, data_hora_deletado, " +
                        "pg_username_delecao, cod_empresa, cod_diagrama, motorizado, origem_cadastro) " +
                        "values ('CHE0001', 215, 1000, true, 64, 1207, 1, '2020-01-21 19:15:31.971416', 5, false, " +
                        "null, null, 3, 2, true, 'INTERNO') returning codigo;");
    }

    private ResultSet insertChecklist(final Statement stmt, final Long codVeiculo) throws SQLException {
        final String sqlChecklist = "insert into public.checklist (cod_unidade, cod_checklist_modelo, " +
                "cod_versao_checklist_modelo, data_hora, data_hora_realizacao_tz_aplicado, " +
                "data_hora_importado_prolog, cpf_colaborador, cod_veiculo, tipo, tempo_realizacao, " +
                "km_veiculo, data_hora_sincronizacao, fonte_data_hora_realizacao, " +
                "versao_app_momento_realizacao, versao_app_momento_sincronizacao, device_id, " +
                "device_imei, device_uptime_realizacao_millis, device_uptime_sincronizacao_millis, " +
                "foi_offline, total_perguntas_ok, total_perguntas_nok, total_alternativas_ok, " +
                "total_alternativas_nok) " +
                "values (215, 1, " +
                "1, '2021-03-08 14:32:59.733039', '2021-03-08 11:32:59.733039', " +
                "null, 3383283194, " + codVeiculo + ", 'S', 27275, " +
                "11331, '2021-04-08 14:32:59.733166', 'SERVIDOR', " +
                "121, 121, '7d6867d2498bd65e', " +
                "null, 0, 211810, false, 0, 3, 1, 7) returning codigo;";
        return stmt.executeQuery(sqlChecklist);
    }

    private void insertRespostasChecklist(final Statement stmt, final Long codChecklist) throws SQLException {
        int codPergunta = 1;
        do {
            final Long codAlternativa = getCodAlternativa(stmt, codPergunta);
            final String sqlInsertChecklistAlternativa =
                    "insert into public.checklist_respostas_nok(cod_unidade," +
                            " cod_checklist_modelo, cod_versao_checklist_modelo, cod_checklist, " +
                            "cod_pergunta, cod_alternativa, resposta_outros) " +
                            "values (215, 1, 1, " + codChecklist + "," + codPergunta + ", " + codAlternativa + ", " +
                            "'teste');";
            stmt.execute(sqlInsertChecklistAlternativa);
            codPergunta++;
        } while (codPergunta < 4);
    }

    @NotNull
    private Long getCodAlternativa(final Statement stmt, final int codPergunta) throws SQLException {
        final String sqlCodAlternativa = "select cap.codigo " +
                "from checklist_alternativa_pergunta cap " +
                "where cap.cod_pergunta = " + codPergunta + " order by cap.ordem limit 1;";
        final ResultSet rSetCodAlternativa = stmt.executeQuery(sqlCodAlternativa);
        if (rSetCodAlternativa.next()) {
            final Long codAlternativa = rSetCodAlternativa.getLong("codigo");
            rSetCodAlternativa.close();
            return codAlternativa;
        } else {
            return null;
        }
    }
}
