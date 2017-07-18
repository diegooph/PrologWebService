package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public final class AvaCorpAvilan extends Sistema {

    public AvaCorpAvilan(@NotNull final Integrador integradorHttp, @NotNull final Integrador integradoDatabase) {
        super(integradorHttp, integradoDatabase);
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return null;
    }
}