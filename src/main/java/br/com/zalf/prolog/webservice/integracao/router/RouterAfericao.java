package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public final class RouterAfericao extends Router {

    private RouterAfericao(@NotNull final IntegracaoDao integracaoDao,
                           @NotNull final IntegradorProLog integradorProLog,
                           @NotNull final String userToken,
                           @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    public static RouterAfericao create(@NotNull final AfericaoDao afericaoDao, @NotNull final String userToken) {
        return new RouterAfericao(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withAfericaoDao(afericaoDao)
                        .build(),
                userToken,
                RecursoIntegrado.AFERICAO);
    }
}
