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

    /**
     * @deprecated at 2019-01-10.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Em seu lugar será utilizado o código da empresa.
     * Utilize {@link #getTiposVeiculosByEmpresa(Long)}.
     */
    @Deprecated
    @NotNull
    List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull final Long codUnidade) throws Exception;

    @NotNull
    List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade, @NotNull final String codTipo) throws Exception;

    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws Exception;
}