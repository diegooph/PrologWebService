package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class Transicao {

    @NotNull
    private final OrigemDestinoEnum origem;
    @NotNull
    private final OrigemDestinoEnum destino;

}
