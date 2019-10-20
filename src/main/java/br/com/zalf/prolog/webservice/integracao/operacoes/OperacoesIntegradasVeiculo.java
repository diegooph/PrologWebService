package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Operações integrados dos veículos.
 */
interface OperacoesIntegradasVeiculo {
    boolean insert(@NotNull final VeiculoCadastro veiculo,
                   @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    boolean update(@NotNull final String placaOriginal,
                   @NotNull final Veiculo veiculo,
                   @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    void updateStatus(@NotNull final Long codUnidade,
                      @NotNull final String placa,
                      @NotNull final Veiculo veiculo,
                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    boolean delete(@NotNull final String placa,
                   @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade,
                                             @Nullable final Boolean ativos) throws Exception;

    @NotNull
    List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade,
                                         @NotNull final String codTipo) throws Exception;

    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws Exception;
}