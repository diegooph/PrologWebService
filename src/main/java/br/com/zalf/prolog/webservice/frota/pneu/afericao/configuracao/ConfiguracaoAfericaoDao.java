package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAlertaColetaSulco;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ConfiguracaoAfericaoDao {

    /**
     * Cria ou atualiza, caso já exista, as configurações de aferição para os tipos de veículo da unidade informada.
     *
     * @param codUnidade    Codigo da {@link Unidade unidade} que os atributos serão alterados.
     * @param configuracoes Novas {@link ConfiguracaoTipoVeiculoAferivel configurações}.
     * @throws Throwable Caso algum erro acorrer.
     */
    void insertOrUpdateConfiguracoesTiposVeiculosAferiveis(
            @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes) throws Throwable;

    /**
     * Busca todas as {@link ConfiguracaoTipoVeiculoAferivel configurações} existentes para os tipos de veículos da
     * {@link Unidade unidade} informada.
     *
     * @param codUnidade Codigo da {@link Unidade unidade} do qual os dados serão buscados.
     * @return Uma {@link List<ConfiguracaoTipoVeiculoAferivel> lista} contendo todos os dados para os tipos de
     * veículos.
     * @throws Throwable Caso algum erro acorrer.
     */
    @NotNull
    List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws Throwable;

    /**
     * Cria ou atualiza, caso já exista, as configurações de alertas na coleta de sulcos para cada
     * {@link Unidade unidade}.
     *
     * @param configuracoes Novas {@link ConfiguracaoAlertaColetaSulco configurações} que serão inseridas ou
     *                      atualizadas.
     * @throws Throwable Caso algum erro acorrer.
     */
    void insertOrUpdateConfiguracoesAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws Throwable;

    /**
     * Busca todas as configurações de alertas na coleta de sulcos das unidades que o {@link Colaborador colaborador}
     * de código informado tem acesso.
     *
     * @param codColaborador Codigo do {@link Colaborador colaborador} do qual serão buscados as configurações para
     *                       cada unidade que ele tem acesso.
     * @throws Throwable Caso algum erro acorrer.
     */
    @NotNull
    List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @NotNull final Long codColaborador) throws Throwable;
}