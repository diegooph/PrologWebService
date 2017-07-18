package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegradorDatabase;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {

    protected AvaCorpAvilan(@NotNull final Integrador integradorHttp) {
        super(integradorHttp);
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull String userToken, @NotNull Long codUnidade) {
        return getIntegradorHttp().getVeiculosAtivosByUnidade(userToken, codUnidade);
    }

    @Override
    protected Integrador getIntegradorDatabase() {
        return new IntegradorDatabase.Builder()
                .withVeiculoDao(Injection.provideVeiculoDao())
                .build();
    }
}