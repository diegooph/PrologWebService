package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OrdemServicoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterChecklistOrdemServico extends Router {

    private RouterChecklistOrdemServico(@NotNull final IntegracaoDao integracaoDao,
                                        @NotNull final IntegradorProLog integradorProLog,
                                        @NotNull final String userToken,
                                        @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    public static RouterChecklistOrdemServico create(@NotNull final OrdemServicoDao ordemServicoDao,
                                                     @NotNull final String userToken) {
        return new RouterChecklistOrdemServico(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withOrdemServicoDao(ordemServicoDao)
                        .build(),
                userToken,
                RecursoIntegrado.CHECKLIST_ORDEM_SERVICO);
    }
}
