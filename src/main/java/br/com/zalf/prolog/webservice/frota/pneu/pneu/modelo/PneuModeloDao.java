package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuMarcaModelo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuModeloDao {

    /**
     * retorna uma lista de marcas de pneus da empresa
     *
     * @param codEmpresa código da empresa
     * @return uma lista de marcas
     * @throws Throwable caso ocorra erro no banco
     */
    List<PneuMarcaModelo> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws Throwable;

    /**
     * insere um modelo de pneu
     *
     * @param pneuModeloInsercao um modelo de pneu
     * @return codigo de inserção do modelo
     * @throws Throwable caso ocorra erro no banco
     */
    Long insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) throws Throwable;

    /**
     * edita modelo de pneu
     *
     * @param pneuModeloEdicao informações de um modelo de pneu
     * @return código do modelo de pneu editado
     * @throws Throwable caso ocorra erro no banco
     */
    Long updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) throws Throwable;

    /**
     * Busca um modelo de pneu a partir de seu código único
     */
    @NotNull
    PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) throws Throwable;
}
