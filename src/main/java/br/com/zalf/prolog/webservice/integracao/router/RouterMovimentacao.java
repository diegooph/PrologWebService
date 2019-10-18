package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.MovimentacaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/10/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterMovimentacao extends Router {
    @NotNull
    public static RouterMovimentacao create(@NotNull final MovimentacaoDao movimentacaoDao,
                                            @NotNull final String userToken) {
        return new RouterMovimentacao(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withMovimentacaoDao(movimentacaoDao)
                        .build(),
                userToken,
                RecursoIntegrado.MOVIMENTACAO);
    }

    private RouterMovimentacao(@NotNull final IntegracaoDao integracaoDao,
                               @NotNull final IntegradorProLog integradorProLog,
                               @NotNull final String userToken,
                               @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}
