package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterPneu extends Router {
    @NotNull
    public static RouterPneu create(@NotNull final PneuDao pneuDao,
                                    @NotNull final String userToken) {
        return new RouterPneu(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withPneuDao(pneuDao)
                        .build(),
                userToken,
                RecursoIntegrado.PNEUS);
    }

    private RouterPneu(@NotNull final IntegracaoDao integracaoDao,
                       @NotNull final IntegradorProLog integradorProLog,
                       @NotNull final String userToken,
                       @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}
