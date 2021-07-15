package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Operações integrados dos veículos.
 */
interface OperacoesIntegradasVeiculo {
    void insert(@NotNull final VeiculoCadastroDto veiculo,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    @NotNull
    InfosVeiculoEditado update(
            @NotNull final Long codColaboradorResponsavelEdicao,
            @NotNull final VeiculoEdicao veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    @NotNull
    List<Veiculo> getVeiculosAtivosByUnidade(@NotNull final Long codUnidade,
                                             @Nullable final Boolean ativos) throws Exception;

    @NotNull
    List<String> getPlacasVeiculosByTipo(@NotNull final Long codUnidade,
                                         @NotNull final String codTipo) throws Exception;

    @NotNull
    List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                final boolean apenasAtivos,
                                                @Nullable final Long codTipoVeiculo) throws Throwable;

    @NotNull
    VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable;

    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa,
                              @NotNull final Long codUnidade,
                              final boolean withPneus) throws Throwable;

    @NotNull
    VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable;
}