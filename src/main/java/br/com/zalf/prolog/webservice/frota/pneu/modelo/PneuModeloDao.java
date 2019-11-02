package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuModeloDao {

    /**
     * Retorna uma lista de marcas de pneu. As marcas de pneu são a nível ProLog.
     *
     * @return uma lista de marcas.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<PneuMarcaListagem> getListagemMarcasPneu() throws Throwable;

    /**
     * Insere um modelo de pneu.
     *
     * @param pneuModeloInsercao um modelo de pneu.
     * @return codigo de inserção do modelo.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    Long insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) throws Throwable;

    /**
     * Edita um modelo de pneu.
     *
     * @param pneuModeloEdicao informações de um modelo de pneu.
     * @return código do modelo de pneu editado.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    Long updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) throws Throwable;

    /**
     * Retorna uma lista de modelos de pneu da empresa.
     *
     * @param codEmpresa código da empresa.
     * @return uma lista de marcas.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<PneuModeloListagem> getListagemModelosPneu(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Busca um modelo de pneu a partir de seu código único.
     *
     * @param codModelo código do modelo para buscar.
     * @return Um pneu para visualização.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) throws Throwable;
}
