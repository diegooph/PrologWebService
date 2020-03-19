package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public @Data class MotivoAtivacaoDesativacao {

    @NotNull
    private final Long codMotivo;

    @NotNull
    private final boolean ativoMotivo;

}
