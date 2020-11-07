package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ItemOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created on 2020-08-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class IntegracaoConverter {
    private IntegracaoConverter() {
        throw new IllegalStateException(IntegracaoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static OsIntegracao createOsIntegracao(@NotNull final ResultSet rSet) throws Throwable {
        return new OsIntegracao(
                rSet.getLong("cod_unidade"),
                rSet.getString("cod_auxiliar_unidade"),
                rSet.getLong("cod_interno_os_prolog"),
                rSet.getLong("cod_os_prolog"),
                rSet.getObject("data_hora_abertura_os", LocalDateTime.class),
                rSet.getString("placa_veiculo"),
                rSet.getLong("km_veiculo_na_abertura"),
                rSet.getString("cpf_colaborador_checklist"),
                StatusOrdemServico.fromString(rSet.getString("status_os")),
                rSet.getObject("data_hora_fechamento_os", LocalDateTime.class),
                new ArrayList<>());
    }

    @NotNull
    public static ItemOsIntegracao createItemOsIntegracao(@NotNull final ResultSet rSet) throws Throwable {
        final StatusItemOrdemServico statusItemOs = StatusItemOrdemServico.fromString(rSet.getString("status_item_os"));
        return new ItemOsIntegracao(
                rSet.getLong("cod_item_os"),
                rSet.getLong("cod_alternativa"),
                rSet.getString("cod_auxiliar_alternativa"),
                rSet.getString("descricao_alternativa"),
                PrioridadeAlternativa.fromString(rSet.getString("prioridade_alternativa")),
                statusItemOs,
                rSet.getBoolean("alternativa_tipo_outros"),
                rSet.getString("descricao_tipo_outros"),
                rSet.getObject("data_hora_fechamento_item_os", LocalDateTime.class),
                rSet.getString("descricao_fechamento_item_os"),
                statusItemOs == StatusItemOrdemServico.PENDENTE ?
                        null : rSet.getLong("km_veiculo_fechamento_item"),
                statusItemOs == StatusItemOrdemServico.PENDENTE ?
                        null : rSet.getObject("data_hora_inicio_resolucao", LocalDateTime.class),
                statusItemOs == StatusItemOrdemServico.PENDENTE ?
                        null : rSet.getObject("data_hora_fim_resolucao", LocalDateTime.class));
    }
}
