package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface RelatorioVeiculoDao {

    /**
     * Método que busca a contagem de veículos ativos de uma listagem de unidades.
     *
     * @param codUnidades - Códigos das unidades que serão filtradas.
     * @return - total de veículos ativos entre as unidades.
     * @throws SQLException - Se algum erro ocorrer na filtragem.
     */
    int getQtdVeiculosAtivos(@NotNull final List<Long> codUnidades) throws SQLException;
}