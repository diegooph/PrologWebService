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
     * @param marcaBanda marca a ser inserida.
     * @return código gerado pelo BD para a nova banda inserida.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    Long insertMarcaBanda(@NotNull final PneuMarcaBandaInsercao marcaBanda) throws Throwable;

    /**
     * Atualiza o nome de uma marca.
     *
     * @param marcaBanda marca com o nome atualizado.
     * @return codigo da marca editada.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    Long updateMarcaBanda(@NotNull final PneuMarcaBandaEdicao marcaBanda) throws Throwable;

    /**
     * Busca as marcas de bandas de uma empresa.
     *
     * @param codEmpresa código da empresa.
     * @return uma lista de marcas de bandas da empresa.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<PneuMarcaBandaListagemVisualizacao> getListagemMarcasBandas(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Busca uma marca de banda específica de uma empresa.
     *
     * @param codMarca código da empresa.
     * @return uma marca de banda da empresa.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    PneuMarcaBandaListagemVisualizacao getMarcaBanda(@NotNull final Long codMarca) throws Throwable;

    /**
     * Insere um novo modelo de banda.
     *
     * @param pneuModeloBandaInsercao modelo de banda a ser inserido.
     * @return código gerado pelo BD para o novo modelo inserido.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    Long insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) throws Throwable;

    /**
     * Edita um modelo de banda.
     *
     * @param modeloBandaEdicao informações de um modelo de banda.
     * @return código do modelo de banda editado.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    Long updateModeloBanda(@NotNull final PneuModeloBandaEdicao modeloBandaEdicao) throws Throwable;

    /**
     * Busca a marca e um modelo de banda específico.
     *
     * @param codModelo código do modelo de uma banda.
     * @return uma objeto contendo a marca e o modelo de uma banda.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    PneuModeloBandaVisualizacao getModeloBanda(@NotNull final Long codModelo) throws Throwable;

    /**
     * Busca os modelos de banda de uma empresa ou marca de banda. Ao menos um dos parâmetros precisa existir.
     *
     * @param codEmpresa código da empresa.
     * @param codMarca código da marca de banda.
     * @return uma lista de marcas contendo os modelos de cada uma.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<PneuModeloBandaListagem> getListagemModelosBandas(@Nullable final Long codEmpresa,
                                                           @Nullable final Long codMarca) throws Throwable;
}
