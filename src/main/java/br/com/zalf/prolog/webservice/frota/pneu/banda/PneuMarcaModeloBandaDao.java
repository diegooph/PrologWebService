package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuMarcaModeloBandaDao {

    /**
     * Insere uma nova marca de banda.
     *
     * @param marcaBanda Marca a ser inserida.
     * @return Código gerado pelo BD para a nova banda inserida.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Long insertMarcaBanda(@NotNull final PneuMarcaBandaInsercao marcaBanda) throws Throwable;

    /**
     * Atualiza o nome de uma marca.
     *
     * @param marcaBanda Marca com o nome atualizado.
     * @return Codigo da marca editada.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Long updateMarcaBanda(@NotNull final PneuMarcaBandaEdicao marcaBanda) throws Throwable;

    /**
     * Busca as marcas de bandas de uma empresa.
     *
     * @param codEmpresa                 Código da empresa.
     * @param comModelos                 True se para cada marca deve retornar seus modelos, false caso contrário.
     * @param incluirMarcasNaoUtilizadas True se devemos buscar também as marcas que a empresa não utiliza,
     *                                   false caso contrário.
     * @return Uma lista de marcas de bandas da empresa.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    List<PneuMarcaBandaListagem> getListagemMarcasBanda(@NotNull final Long codEmpresa,
                                                        final boolean comModelos,
                                                        final boolean incluirMarcasNaoUtilizadas) throws Throwable;

    /**
     * Busca uma marca de banda específica de uma empresa.
     *
     * @param codMarca Código da empresa.
     * @return Uma marca de banda da empresa.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    PneuMarcaBandaVisualizacao getMarcaBanda(@NotNull final Long codMarca) throws Throwable;

    /**
     * Insere um novo modelo de banda.
     *
     * @param pneuModeloBandaInsercao Modelo de banda a ser inserido.
     * @return Código gerado pelo BD para o novo modelo inserido.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Long insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) throws Throwable;

    /**
     * Edita um modelo de banda.
     *
     * @param modeloBandaEdicao Informações de um modelo de banda.
     * @return Código do modelo de banda editado.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Long updateModeloBanda(@NotNull final PneuModeloBandaEdicao modeloBandaEdicao) throws Throwable;

    /**
     * Busca a marca e um modelo de banda específico.
     *
     * @param codModelo Código do modelo de uma banda.
     * @return Uma objeto contendo a marca e o modelo de uma banda.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    PneuModeloBandaVisualizacao getModeloBanda(@NotNull final Long codModelo) throws Throwable;

    /**
     * Busca os modelos de banda de uma empresa ou de uma marca específica. O {@code codEmpresa} é obrigatório.
     *
     * @param codEmpresa Código da empresa.
     * @param codMarca   Código da marca de banda.
     * @return Uma lista de marcas contendo os modelos de cada uma.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    List<PneuModeloBandaListagem> getListagemModelosBandas(@NotNull final Long codEmpresa,
                                                           @Nullable final Long codMarca) throws Throwable;
}
