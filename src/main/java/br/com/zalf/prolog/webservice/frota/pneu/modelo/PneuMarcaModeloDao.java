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
     * @param codEmpresa código da empresa.
     * @param comModelos true se para cada marca deve retornar seus modelos, false caso contrário.
     * @return uma lista de marcas.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<PneuMarcaListagem> getListagemMarcasPneu(@NotNull final Long codEmpresa,
                                                  final boolean comModelos) throws Throwable;

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
     * Busca os modelos de pneu de uma empresa ou marca de pneu. Ao menos um dos parâmetros precisa existir.
     *
     * @param codEmpresa código da empresa.
     * @param codMarca código da marca de pneu.
     * @return uma lista de modelos.
     * @throws Throwable caso ocorra algum erro.
     */
    @NotNull
    List<PneuModeloListagem> getListagemModelosPneu(@Nullable final Long codEmpresa,
                                                    @Nullable final Long codMarca) throws Throwable;

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
