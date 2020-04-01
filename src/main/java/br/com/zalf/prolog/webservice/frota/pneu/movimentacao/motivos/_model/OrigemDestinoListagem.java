package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class OrigemDestinoListagem {

    @NotNull
    private final Long codUnidade;

    @NotNull
    private final OrigemDestinoEnum origem;

    @NotNull
    private final OrigemDestinoEnum destino;

    @NotNull
    private final boolean obrigatorio;

}
