package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoAvulsa;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosTipoVeiculoConfiguracaoAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosUnidadeRestricao;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SistemaProtheusNepomucenoDao {

    @NotNull
    List<Long> getApenasUnidadesMapeadas(@NotNull final Connection conn,
                                         @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final String codAuxiliarUnidade,
                @NotNull final Afericao afericao) throws Throwable;

    @NotNull
    List<InfosAfericaoAvulsa> getInfosAfericaoAvulsa(@NotNull final Connection conn,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final List<String> codPneus) throws Throwable;

    @NotNull
    Map<String, InfosUnidadeRestricao> getInfosUnidadeRestricao(@NotNull final Connection conn,
                                                                @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Table<String, String, InfosTipoVeiculoConfiguracaoAfericao> getInfosTipoVeiculoConfiguracaoAfericao(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Map<String, InfosAfericaoRealizadaPlaca> getInfosAfericaoRealizadaPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final List<String> placasNepomuceno) throws Throwable;

    @NotNull
    ConfiguracaoNovaAfericaoPlaca getConfigNovaAfericaoPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final String codEstruturaVeiculo) throws Throwable;

    @NotNull
    ConfiguracaoNovaAfericaoAvulsa getConfigNovaAfericaoAvulsa(@NotNull final Connection conn,
                                                               @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    Map<String, Integer> getMapeamentoPosicoesProlog(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final String codEstruturaVeiculo) throws Throwable;

    @NotNull
    Short getCodDiagramaByCodEstrutura(@NotNull final Connection conn,
                                       @NotNull final Long codEmpresa,
                                       @NotNull final String codEstruturaVeiculo) throws Throwable;

    @NotNull
    Map<Long, String> getCodFiliais(@NotNull final Connection conn,
                                    @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    List<String> verificaCodAuxiliarTipoVeiculoValido(@Nullable final Long codEmpresaTipoVeiculo,
                                                      @Nullable final Long codTipoVeiculo) throws Throwable;
}
