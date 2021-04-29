package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public final class RouterVeiculo extends Router {

    private RouterVeiculo(@NotNull final IntegracaoDao integracaoDao,
                          @NotNull final IntegradorProLog integradorProLog,
                          @NotNull final String userToken,
                          @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    public static RouterVeiculo create(@NotNull final VeiculoDao veiculoDao,
                                       @NotNull final String userToken) {
        return new RouterVeiculo(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withVeiculoDao(veiculoDao)
                        .build(),
                userToken,
                RecursoIntegrado.VEICULOS);
    }
}