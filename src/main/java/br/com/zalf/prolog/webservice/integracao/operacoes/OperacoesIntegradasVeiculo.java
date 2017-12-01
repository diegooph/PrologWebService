package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Operações integrados dos veículos.
 */
interface OperacoesIntegradasVeiculo {
    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade) throws Exception;

    @NotNull
    List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull final Long codUnidade) throws Exception;

    @NotNull
    List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade, @NotNull final String codTipo) throws Exception;

    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws Exception;
}