package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaModeloBandaService {
    @NotNull
    private static final String TAG = PneuModeloBandaEdicao.class.getSimpleName();
    @NotNull
    private final PneuMarcaModeloBandaDao dao = Injection.providePneuModeloBandaDao();

    @NotNull
    ResponseWithCod insertMarcaBanda(@NotNull final PneuMarcaBandaInsercao marcaBanda) {
        try {
            return ResponseWithCod.ok(
                    "Marca de banda inserida com sucesso",
                    dao.insertMarcaBanda(marcaBanda));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir marca de banda para empresa: " + marcaBanda.getCodEmpresa(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir marca da banda, tente novamente");
        }
    }

    @NotNull
    ResponseWithCod updateMarcaBanda(@NotNull final PneuMarcaBandaEdicao marcaBanda) {
        try {
            return ResponseWithCod.ok("Marca de banda editada com sucesso", dao.updateMarcaBanda(marcaBanda));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar marca de banda da empresa: " + marcaBanda.getCodigo(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar marca de banda, tente novamente");
        }
    }

    @NotNull
    List<PneuMarcaBandaListagem> getListagemMarcasBanda(@NotNull final Long codEmpresa,
                                                        final boolean comModelos,
                                                        final boolean incluirMarcasNaoUtilizadas) {
        try {
            return dao.getListagemMarcasBanda(codEmpresa, comModelos, incluirMarcasNaoUtilizadas);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de marcas de banda:\n" +
                    "codEmpresa: " + codEmpresa + "\n" +
                    "comModelos: " + comModelos + "\n" +
                    "incluirMarcasNaoUtilizadas: " + incluirMarcasNaoUtilizadas, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcas de banda, tente novamente");
        }
    }

    @NotNull
    PneuMarcaBandaVisualizacao getMarcaBanda(@NotNull final Long codMarca) {
        try {
            return dao.getMarcaBanda(codMarca);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marca de banda: " + codMarca, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marca de banda, tente novamente");
        }
    }

    @NotNull
    ResponseWithCod insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) {
        try {
            return ResponseWithCod.ok("Modelo de banda inserido com sucesso",
                    dao.insertModeloBanda(pneuModeloBandaInsercao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de banda para marca: " + pneuModeloBandaInsercao.getCodMarca() +
                    " Empresa: " + pneuModeloBandaInsercao.getCodEmpresa(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir modelo de banda, tente novamente");
        }
    }

    @NotNull
    ResponseWithCod updateModeloBanda(@NotNull final PneuModeloBandaEdicao pneuModeloBandaEdicao) {
        try {
            return ResponseWithCod.ok("Modelo de banda editado com sucesso",
                    dao.updateModeloBanda(pneuModeloBandaEdicao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao editar a banda", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao editar modelo de banda, tente novamente");
        }
    }

    @NotNull
    List<PneuModeloBandaListagem> getListagemModelosBandas(@Nullable final Long codEmpresa,
                                                           @Nullable final Long codMarca) {
        try {
            if (codEmpresa == null) {
                throw new RuntimeException("codEmpresa nunca pode ser null!");
            }

            return dao.getListagemModelosBandas(codEmpresa, codMarca);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de modelos de banda:\n"
                    + "codEmpresa: " + codEmpresa + "\n"
                    + "codMarca: " + codMarca, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os modelos de banda, tente novamente");
        }
    }

    @NotNull
    PneuModeloBandaVisualizacao getModeloBanda(@NotNull final Long codModelo) {
        try {
            return dao.getModeloBanda(codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar modelo de banda da empresa: " + codModelo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar modelo de banda, tente novamente");
        }
    }
}