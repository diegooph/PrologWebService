package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data
class MotivoRetiradaEdicao {

    @NotNull
    private final Long codMotivoRetirada;

    @NotNull
    private final String descricaoMotivoRetirada;

    @NotNull
    private final boolean ativoMotivoRetirada;

    @Nullable
    private final String codAuxiliarMotivoRetirada;

}
