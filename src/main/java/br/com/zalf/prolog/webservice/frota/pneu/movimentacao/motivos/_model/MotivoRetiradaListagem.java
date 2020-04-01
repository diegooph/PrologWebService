package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-01
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class MotivoRetiradaListagem {

    @NotNull
    private final Long codMotivoRetirada;

    @NotNull
    private final String descricaoMotivoRetirada;

    @NotNull
    private final boolean ativo;

}
