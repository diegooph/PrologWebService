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

    @NotNull
    List<PneuMarcaListagem> getListagemMarcasPneu(@NotNull final Long codEmpresa,
                                                  final boolean comModelos,
                                                  final boolean incluirMarcasNaoUtilizadas) throws Throwable;

    @NotNull
    Long insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) throws Throwable;

    @NotNull
    Long updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) throws Throwable;

    @NotNull
    List<PneuModeloListagem> getListagemModelosPneu(@NotNull final Long codEmpresa,
                                                    @Nullable final Long codMarca) throws Throwable;

    @NotNull
    PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) throws Throwable;
}
