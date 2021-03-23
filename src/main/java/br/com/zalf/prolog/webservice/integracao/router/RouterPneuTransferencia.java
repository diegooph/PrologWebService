package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 17/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterPneuTransferencia extends Router {
    private RouterPneuTransferencia(@NotNull final IntegracaoDao integracaoDao,
                                    @NotNull final IntegradorProLog integradorProLog,
                                    @NotNull final String userToken,
                                    @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    @NotNull
    public static RouterPneuTransferencia create(@NotNull final PneuTransferenciaDao pneuTransferenciaDao,
                                                 @NotNull final String userToken) {
        return new RouterPneuTransferencia(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withPneuTransferenciaDao(pneuTransferenciaDao)
                        .build(),
                userToken,
                RecursoIntegrado.PNEU_TRANSFERENCIA);
    }
}
