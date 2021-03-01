package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterModeloChecklist extends Router {

    private RouterModeloChecklist(@NotNull final IntegracaoDao integracaoDao,
                                  @NotNull final IntegradorProLog integradorProLog,
                                  @NotNull final String userToken,
                                  @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    public static RouterModeloChecklist create(@NotNull final ChecklistModeloDao checklistModeloDao,
                                               @NotNull final String userToken) {
        return new RouterModeloChecklist(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withChecklistModeloDao(checklistModeloDao)
                        .build(),
                userToken,
                RecursoIntegrado.CHECKLIST_MODELO);
    }
}