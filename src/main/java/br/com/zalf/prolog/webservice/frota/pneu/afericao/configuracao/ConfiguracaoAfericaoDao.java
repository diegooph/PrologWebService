package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.ConfiguracaoTipoVeiculoAfericao;
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
     * @param configuracoes - Novas {@link ConfiguracaoTipoVeiculoAfericao}s que serão inseridas.
     * @throws SQLException - Caso algum erro acorrer.
     */
    void insertOrUpdateConfiguracao(@NotNull final Long codUnidade,
                                    @NotNull final List<ConfiguracaoTipoVeiculoAfericao> configuracoes) throws SQLException;

    /**
     * Busca todas as {@link ConfiguracaoTipoVeiculoAfericao} existentes para os tipos de veículos da {@link Unidade}.
     *
     * @param codUnidade - Codigo da {@link Unidade} que os dados serão buscados.
     * @return - Uma {@link List<ConfiguracaoTipoVeiculoAfericao>} contendo todos os dados para os tipos de veículos.
     * @throws SQLException - Caso algum erro acorrer.
     */
    List<ConfiguracaoTipoVeiculoAfericao> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws SQLException;
}
