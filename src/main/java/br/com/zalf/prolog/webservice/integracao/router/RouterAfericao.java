package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public class RouterAfericao extends Router {

    public static RouterAfericao create(@NotNull AfericaoDao afericaoDao, @NotNull String userToken) {
        return new RouterAfericao(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder()
                        .withAfericaoDao(afericaoDao)
                        .build(),
                userToken,
                RecursoIntegrado.AFERICAO);
    }

    private RouterAfericao(@NotNull IntegracaoDao integracaoDao,
                           @NotNull IntegradorProLog integradorProLog,
                           @NotNull String userToken,
                           @NotNull RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}
