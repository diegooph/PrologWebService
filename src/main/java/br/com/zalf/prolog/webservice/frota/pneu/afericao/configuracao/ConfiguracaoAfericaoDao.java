package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.model.ConfiguracaoAlertaColetaSulco;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao.model.ConfiguracaoTipoVeiculoAferivel;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ConfiguracaoAfericaoDao {

    /**
     * Método utilizado para alterar uma configuração de aferição de um tipo de veículo.
     *
     * @param codUnidade    - Codigo da {@link Unidade} que os atributos serão alterados.
     * @param configuracoes - Novas {@link ConfiguracaoTipoVeiculoAferivel}s que serão inseridas.
     * @throws SQLException - Caso algum erro acorrer.
     */
    void insertOrUpdateConfiguracoesTiposVeiculosAferiveis(@NotNull final Long codUnidade,
                                                           @NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes)
            throws Throwable;

    /**
     * Busca todas as {@link ConfiguracaoTipoVeiculoAferivel} existentes para os tipos de veículos da {@link Unidade}.
     *
     * @param codUnidade - Codigo da {@link Unidade} que os dados serão buscados.
     * @return - Uma {@link List< ConfiguracaoTipoVeiculoAferivel >} contendo todos os dados para os tipos de veículos.
     * @throws SQLException - Caso algum erro acorrer.
     */
    @NotNull
    List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(@NotNull final Long codUnidade)
            throws Throwable;


    void insertOrUpdateConfiguracoesAlertaColetaSulco(@NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes)
            throws Throwable;

    @NotNull
    List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(@NotNull final Long codColaborador)
            throws Throwable;
}