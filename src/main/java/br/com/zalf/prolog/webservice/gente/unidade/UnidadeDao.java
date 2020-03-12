package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface UnidadeDao {

    /**
     * Busca uma unidade baseado no seu código.
     *
     * @param codUnidade um código de uma unidade.
     * @return um {@link UnidadeVisualizacao}.
     * @throws SQLException caso ocorrer erro no banco.
     */
    @NotNull
    UnidadeVisualizacao getUnidadeByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * Busca todas as unidades baseado no código da empresa.
     *
     * @param codEmpresa um código de uma unidade.
     * @return um {@link List<UnidadeVisualizacao>}.
     * @throws SQLException caso ocorrer erro no banco.
     */
    @NotNull
    List<UnidadeVisualizacao> getAllUnidadeByCodEmpresa(Long codEmpresa) throws SQLException;

    /**
     * Busca todas as unidades baseado no código da empresa e da regional.
     *
     * @param codEmpresa  um código de uma empresa;
     * @param codRegional um código de uma regional.
     * @return um {@link List<UnidadeVisualizacao>}.
     * @throws SQLException caso ocorrer erro no banco.
     */
    @NotNull
    List<UnidadeVisualizacao> getAllUnidadeByCodEmpresaAndCodRegional(Long codEmpresa, Long codRegional) throws SQLException;

}
