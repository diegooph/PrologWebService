package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 17/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RouterVeiculoTransferencia extends Router {
    private RouterVeiculoTransferencia(@NotNull final IntegracaoDao integracaoDao,
                                       @NotNull final IntegradorProLog integradorProLog,
                                       @NotNull final String userToken,
                                       @NotNull final RecursoIntegrado recursoIntegrado) {
        super(integracaoDao, integradorProLog, userToken, recursoIntegrado);
    }

    @NotNull
    public static RouterVeiculoTransferencia create(@NotNull final VeiculoTransferenciaDao veiculoTransferenciaDao,
                                                    @NotNull final String userToken) {
        return new RouterVeiculoTransferencia(
                Injection.provideIntegracaoDao(),
                new IntegradorProLog.Builder(userToken)
                        .withVeiculoTransferenciaDao(veiculoTransferenciaDao)
                        .build(),
                userToken,
                RecursoIntegrado.VEICULO_TRANSFERENCIA);
    }
}
