package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public final class RouterVeiculo extends Router {

    public static RouterVeiculo create(@NotNull VeiculoDao veiculoDao, @NotNull String userToken) {
        return new RouterVeiculo(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder()
                        .withVeiculoDao(veiculoDao)
                        .build(),
                userToken,
                RecursoIntegrado.VEICULOS);
    }

    private RouterVeiculo(@NotNull IntegracaoDao integracaoDao,
                           @NotNull IntegradorProLog integradorProLog,
                           @NotNull String userToken,
                           @NotNull RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }
}