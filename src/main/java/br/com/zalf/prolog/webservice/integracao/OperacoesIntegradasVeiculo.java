package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by luiz on 7/17/17.
 */
public interface OperacoesIntegradasVeiculo {
    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade) throws Exception;
}