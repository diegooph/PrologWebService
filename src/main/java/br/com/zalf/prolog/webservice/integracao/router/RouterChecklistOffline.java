package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.offline.ChecklistOfflineDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-04-08
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterChecklistOffline extends Router {

    public static RouterChecklistOffline create(@NotNull final ChecklistOfflineDao checklistOfflineDao,
                                                @NotNull final String userToken) {
        return new RouterChecklistOffline(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withChecklistOfflineDao(checklistOfflineDao)
                        .withTipoVeiculoDao(Injection.provideTipoVeiculoDao())
                        .build(),
                userToken,
                RecursoIntegrado.CHECKLIST_OFFLINE);
    }

    private RouterChecklistOffline(@NotNull final IntegracaoDao integracaoDao,
                                   @NotNull final IntegradorProLog integradorProLog,
                                   @NotNull final String userToken,
                                   @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}
