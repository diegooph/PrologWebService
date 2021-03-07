package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-23
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterTipoVeiculo extends Router {

    private RouterTipoVeiculo(@NotNull final IntegracaoDao integracaoDao,
                              @NotNull final IntegradorProLog integradorProLog,
                              @NotNull final String userToken,
                              @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    public static RouterTipoVeiculo create(@NotNull final TipoVeiculoDao tipoVeiculoDao,
                                           @NotNull final String userToken) {
        return new RouterTipoVeiculo(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withTipoVeiculoDao(tipoVeiculoDao)
                        .build(),
                userToken,
                RecursoIntegrado.TIPO_VEICULO);
    }
}
