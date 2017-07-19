package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.integrador.Integrador;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public interface OperacoesIntegradasVeiculo extends OperacoesIntegradas {
    @NotNull
    default List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade) throws Exception {
        final Integrador integrador = getIntegradorDatabase();
        return integrador.getVeiculosAtivosByUnidade(codUnidade);
    }
}