package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Operações referentes a veículos que possuem integração com algum sistema.
 * Todas as operações devem ter como parâmetro ao menos o token
 */
interface OperacoesIntegradasVeiculo {
    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade) throws Exception;
}