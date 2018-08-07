package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.ChecklistFarol;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.FarolVeiculoDia;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class ChecklistConverter {

    private ChecklistConverter() {
        throw new IllegalStateException(ChecklistConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static FarolChecklist createFarolChecklist(@NotNull final ResultSet rSet) throws SQLException {
        final List<FarolVeiculoDia> farolVeiculoDias = new ArrayList<>();
        String placaAntiga = null, placaAtual = null;
        while (rSet.next()) {
            if (rSet.getLong("CODIGO_PERGUNTA") == 0) {
                // Verificamos se existem perguntas. Se não, podemos instanciar apenas as informações do Checklist
                farolVeiculoDias.add(createFarolVeiculoDiaSemItensCriticos(rSet));
            } else {
                // TODO - Tratar lógica de troca de placas aqui
            }

        }
        return new FarolChecklist(farolVeiculoDias);
    }

    @NotNull
    private static FarolVeiculoDia createFarolVeiculoDiaSemItensCriticos(@NotNull final ResultSet rSet)
            throws SQLException {
        final String placa = rSet.getString("PLACA");

        ChecklistFarol checkSaida = null;
        final Long codChecklistSaida = rSet.getLong("COD_CHECKLIST_SAIDA");
        if (!rSet.wasNull()) {
            checkSaida = new ChecklistFarol(
                    codChecklistSaida,
                    rSet.getString("COLABORADOR_SAIDA"),
                    rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_SAIDA", LocalDateTime.class),
                    Checklist.TIPO_SAIDA);
        }
        ChecklistFarol checkRetorno = null;
        final Long codChecklistRetorno = rSet.getLong("COD_CHECKLIST_RETORNO");
        if (!rSet.wasNull()) {
            checkRetorno = new ChecklistFarol(
                    codChecklistRetorno,
                    rSet.getString("COLABORADOR_RETORNO"),
                    rSet.getObject("DATA_HORA_ULTIMO_CHECKLIST_RETORNO", LocalDateTime.class),
                    Checklist.TIPO_RETORNO);
        }
        return new FarolVeiculoDia(placa, checkSaida, checkRetorno, new ArrayList<>());
    }
}