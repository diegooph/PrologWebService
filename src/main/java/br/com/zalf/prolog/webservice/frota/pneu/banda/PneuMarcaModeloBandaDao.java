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

    @NotNull
    Long insertMarcaBanda(@NotNull final PneuMarcaBandaInsercao marcaBanda) throws Throwable;

    @NotNull
    Long updateMarcaBanda(@NotNull final PneuMarcaBandaEdicao marcaBanda) throws Throwable;

    @NotNull
    List<PneuMarcaBandaListagem> getListagemMarcasBanda(@NotNull final Long codEmpresa,
                                                        final boolean comModelos,
                                                        final boolean incluirMarcasNaoUtilizadas) throws Throwable;

    @NotNull
    PneuMarcaBandaVisualizacao getMarcaBanda(@NotNull final Long codMarca) throws Throwable;

    @NotNull
    Long insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) throws Throwable;

    @NotNull
    Long updateModeloBanda(@NotNull final PneuModeloBandaEdicao modeloBandaEdicao) throws Throwable;

    @NotNull
    PneuModeloBandaVisualizacao getModeloBanda(@NotNull final Long codModelo) throws Throwable;

    @NotNull
    List<PneuModeloBandaListagem> getListagemModelosBandas(@NotNull final Long codEmpresa,
                                                           @Nullable final Long codMarca) throws Throwable;
}
