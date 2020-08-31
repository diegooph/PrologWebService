package br.com.zalf.prolog.webservice.integracao.avacorpavilan._model;

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
                new ArrayList<>());
    }

    @NotNull
    public static ItemOsIntegracao createItemOsIntegracao(@NotNull final ResultSet rSet) throws Throwable {
        return new ItemOsIntegracao(
                rSet.getLong("cod_item_os"),
                rSet.getLong("cod_alternativa"),
                rSet.getString("cod_auxiliar_alternativa"),
                rSet.getString("descricao_alternativa"),
                rSet.getObject("data_hora_fechamento_item_os", LocalDateTime.class),
                rSet.getString("descricao_fechamento_item_os"));
    }
}
