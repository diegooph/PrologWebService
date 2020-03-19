package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data class MotivoEdicao {

    @NotNull
    private final String descricaoMotivo;

    @NotNull
    private final Boolean ativoMotivo;

}
