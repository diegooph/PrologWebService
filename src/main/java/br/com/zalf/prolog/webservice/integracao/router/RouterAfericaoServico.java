package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterAfericaoServico extends Router {
    private RouterAfericaoServico(@NotNull final IntegracaoDao integracaoDao,
                                  @NotNull final IntegradorProLog integradorProLog,
                                  @NotNull final String userToken,
                                  @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    @NotNull
    public static RouterAfericaoServico create(@NotNull final ServicoDao afericaoServicoDao,
                                               @NotNull final String userToken) {
        return new RouterAfericaoServico(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withAfericaoServicoDao(afericaoServicoDao)
                        .build(),
                userToken,
                RecursoIntegrado.AFERICAO_SERVICO);
    }
}
