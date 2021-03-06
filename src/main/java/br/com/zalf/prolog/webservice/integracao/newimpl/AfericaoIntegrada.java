package br.com.zalf.prolog.webservice.integracao.newimpl;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-04-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AfericaoIntegrada {

    @NotNull
    Long insertAfericao(@NotNull final Long codUnidade,
                        @NotNull final Afericao afericao,
                        final boolean deveAbrirServico) throws Throwable;
}
