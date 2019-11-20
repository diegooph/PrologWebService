package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuMarcaModeloDao {
    /**
     * Retorna uma lista de marcas de pneu. As marcas de pneu são a nível ProLog, por enquanto. Como em breve será
     * por empresa, o método já está preparado recebendo o código da empresa.
     *
     * @param codEmpresa                 Código da empresa.
     * @param comModelos                 True se para cada marca deve retornar seus modelos, false caso contrário.
     * @param incluirMarcasNaoUtilizadas True se devemos buscar também as marcas que a empresa não utiliza,
     *                                   false caso contrário.
     * @return Uma lista de marcas.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    List<PneuMarcaListagem> getListagemMarcasPneu(@NotNull final Long codEmpresa,
                                                  final boolean comModelos,
                                                  final boolean incluirMarcasNaoUtilizadas) throws Throwable;

    /**
     * Insere um modelo de pneu.
     *
     * @param pneuModeloInsercao Um modelo de pneu.
     * @return Codigo de inserção do modelo.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Long insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) throws Throwable;

    /**
     * Edita um modelo de pneu.
     *
     * @param pneuModeloEdicao Informações de um modelo de pneu.
     * @return Código do modelo de pneu editado.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Long updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) throws Throwable;

    /**
     * Busca os modelos de pneu de uma empresa ou de umna marca de pneu. O {@code codEmpresa} é obrigatório.
     *
     * @param codEmpresa Código da empresa.
     * @param codMarca   Código da marca de pneu.
     * @return Uma lista de modelos.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    List<PneuModeloListagem> getListagemModelosPneu(@NotNull final Long codEmpresa,
                                                    @Nullable final Long codMarca) throws Throwable;

    /**
     * Busca um modelo de pneu a partir de seu código único.
     *
     * @param codModelo Código do modelo para buscar.
     * @return Um pneu para visualização.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) throws Throwable;
}
