package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public final class RouterChecklists extends Router {

    public static RouterChecklists create(@NotNull ChecklistDao checklistDao, @NotNull String userToken) {
        return new RouterChecklists(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder()
                        .withChecklistDao(checklistDao)
                        .build(),
                userToken,
                RecursoIntegrado.CHECKLIST);
    }

    private RouterChecklists(@NotNull IntegracaoDao integracaoDao,
                             @NotNull IntegradorProLog integradorProLog,
                             @NotNull String userToken,
                             @NotNull RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}