package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Operações integrados dos veículos.
 */
interface OperacoesIntegradasVeiculo {
    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade, @Nullable final Boolean ativos) throws Exception;

    @NotNull
    List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade, @NotNull final String codTipo) throws Exception;

    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws Exception;
}