package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created on 2020-03-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data
class MotivoRetiradaOrigemDestinoListagemMotivos {

    @NotNull
    private final Long codUnidade;

    @NotNull
    private final String nomeUnidade;

    @NotNull
    private final List<MotivoRetiradaOrigemDestinoMotivos> origensDestinos;

}
