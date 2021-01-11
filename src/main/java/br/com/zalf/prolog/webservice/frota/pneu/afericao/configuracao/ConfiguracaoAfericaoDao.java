package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ConfiguracaoAfericaoDao {

    void insertOrUpdateConfiguracoesTiposVeiculosAferiveis(
            @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivelInsercao> configuracoes) throws Throwable;

    @NotNull
    List<ConfiguracaoTipoVeiculoAferivelListagem> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws Throwable;

    void insertOrUpdateConfiguracoesAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws Throwable;

    @NotNull
    List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @NotNull final Long codColaborador) throws Throwable;

    void upsertConfiguracoesCronogramaServicos(
            @NotNull final Long codColaborador,
            @NotNull final List<ConfiguracaoCronogramaServicoUpsert> configuracoes) throws Throwable;

    @NotNull
    List<ConfiguracaoCronogramaServico> getConfiguracoesCronogramaServicos(
            @NotNull final Long codColaborador) throws Throwable;

    @NotNull
    List<ConfiguracaoCronogramaServicoHistorico> getConfiguracoesCronogramaServicosHistorico(
            @NotNull final Long codPneuRestricao) throws Throwable;
}