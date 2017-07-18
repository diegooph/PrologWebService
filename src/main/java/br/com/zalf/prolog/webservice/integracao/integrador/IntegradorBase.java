package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class IntegradorBase implements Integrador {

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull String userToken, @NotNull Long codUnidade) {
        throw new UnsupportedOperationException();
    }
}