package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface UnidadeDao {

    /**
     * Atualiza os dados de uma {@link UnidadeEdicao unidade}.
     *
     * @param unidade Dados da unidade a ser atualizada.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    void update(@NotNull final UnidadeEdicao unidade) throws Throwable;

    /**
     * Busca uma unidade baseado no seu código.
     *
     * @param codUnidade um código de uma unidade.
     * @return uma {@link UnidadeVisualizacaoListagem unidade}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    UnidadeVisualizacaoListagem getUnidadeByCodigo(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca todas as unidades baseado no código da empresa e da regional.
     * <p>
     * O código da regional pode ser {@code null}, significando que o usuário quer trazer de todas as regionais.
     *
     * @param codEmpresa  um código de uma empresa;
     * @param codRegional um código de uma regional.
     * @return um {@link List< UnidadeVisualizacaoListagem >}.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<UnidadeVisualizacaoListagem> getUnidadesListagem(@NotNull final Long codEmpresa,
                                                          @Nullable final Long codRegional) throws Throwable;

}
