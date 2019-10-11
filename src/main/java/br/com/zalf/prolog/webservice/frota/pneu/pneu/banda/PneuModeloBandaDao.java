package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.*;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 19/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuModeloBandaDao {

    /**
     * Busca as marcas e modelos de bandas de uma empresa
     *
     * @param codEmpresa código da empresa
     * @return uma lista de marcas contendo os modelos de cada uma
     * @throws SQLException caso não seja possivel realizar a busca
     */
    List<PneuMarcaBandas> listagemMarcasModelosBandas(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Busca a marca e um modelo de banda específico
     *
     * @param codModelo código do modelo de uma banda
     * @return uma objeto contendo a marca e o modelo de uma banda
     * @throws SQLException caso não seja possivel realizar a busca
     */
    PneuMarcaBanda getMarcaModeloBanda(@NotNull final Long codModelo) throws Throwable;

    /**
     * Insere uma nova marca de banda
     *
     * @param marca      marca a ser inserida
     * @param codEmpresa código da empresa a ser vinculada a marca
     * @return código gerado pelo BD para a nova banda inserida
     * @throws SQLException
     */
    Long insertMarcaBanda(@NotNull final PneuMarcaBandas marca,
                          @NotNull final Long codEmpresa) throws Throwable;

    /**
     * Atualiza o nome de uma marca
     *
     * @param marca      marca com o nome atualizado
     * @param codEmpresa código da empresa na qual a marca pertence
     * @return
     * @throws SQLException
     */
    boolean updateMarcaBanda(@NotNull final PneuMarcaBandas marca,
                             @NotNull final Long codEmpresa) throws Throwable;

    /**
     * Insere um novo modelo de banda
     *
     * @param pneuModeloBandaInsercao modelo de banda a ser inserido
     * @return código gerado pelo BD para o novo modelo inserido
     * @throws Throwable
     */
    Long insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) throws Throwable;

    /**
     * edita modelo de banda
     *
     * @param modeloBandaEdicao informações de um modelo de banda
     * @return código do modelo de banda editado
     * @throws Throwable caso ocorra erro no banco
     */
    Long updateModeloBanda(@NotNull final PneuModeloBandaEdicao modeloBandaEdicao) throws Throwable;
}
