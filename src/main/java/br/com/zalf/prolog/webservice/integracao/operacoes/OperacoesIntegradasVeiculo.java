package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Operações integrados dos veículos.
 */
interface OperacoesIntegradasVeiculo {
    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade) throws Exception;
}