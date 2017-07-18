package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull String userToken, @NotNull Long codUnidade) {
        return getIntegradorHttp().getVeiculosAtivosByUnidade(userToken, codUnidade);
    }
}