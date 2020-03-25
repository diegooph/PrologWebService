package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created on 2020-03-25
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data
class MotivoRetiradaOrigemDestinoBatch {

    @NotNull
    private final OrigemDestinoEnum origem;

    @NotNull
    private final OrigemDestinoEnum destino;

    @NotNull
    private final boolean obrigatorio;

    @NotNull
    private final List<Long> codMotivos;

}
