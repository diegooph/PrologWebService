package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegradorDatabase;
import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public final class RouterVeiculo extends Router {

    public static RouterVeiculo create(@NotNull VeiculoDao veiculoDao, @NotNull String userToken) {
        return new RouterVeiculo(
                Injection.provideIntegracaoDao(),
                new IntegradorDatabase.Builder()
                        .withVeiculoDao(veiculoDao)
                        .build(),
                userToken,
                RecursoIntegrado.VEICULOS);
    }

    private RouterVeiculo(@NotNull IntegracaoDao integracaoDao,
                         @NotNull Integrador integradorDatabase,
                         @NotNull String userToken,
                         @NotNull RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorDatabase, userToken, recursoIntegrado);
    }
}