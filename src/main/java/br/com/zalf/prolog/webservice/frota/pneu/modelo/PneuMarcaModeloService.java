package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuModeloBandaEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuMarcaModeloService {
    private static final String TAG = PneuModeloBandaEdicao.class.getSimpleName();
    @NotNull
    private final PneuMarcaModeloDao dao = Injection.providePneuModeloDao();

    @NotNull
    List<PneuMarcaListagem> getListagemMarcasPneu(@NotNull final Long codEmpresa,
                                                  final boolean comModelos,
                                                  final boolean incluirMarcasNaoUtilizadas) {
        try {
            return dao.getListagemMarcasPneu(codEmpresa, comModelos, incluirMarcasNaoUtilizadas);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de marcas de pneu:\n" +
                    "codEmpresa: " + codEmpresa + "\n" +
                    "comModelos: " + comModelos + "\n" +
                    "incluirMarcasNaoUtilizadas: " + incluirMarcasNaoUtilizadas, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar as marcas de pneu, tente novamente");
        }
    }

    @NotNull
    ResponseWithCod insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) {
        try {
            return ResponseWithCod.ok(
                    "Modelo de pneu inserido com sucesso",
                    dao.insertModeloPneu(pneuModeloInsercao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de pneu.\n" +
                    "Empresa: " + pneuModeloInsercao.getCodEmpresa() + "\n" +
                    "Marca: " + pneuModeloInsercao.getCodMarca(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o modelo de pneu, tente novamente");
        }
    }

    @NotNull
    ResponseWithCod updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) {
        try {
            return ResponseWithCod.ok(
                    "Modelo de pneu editado com sucesso",
                    dao.updateModeloPneu(pneuModeloEdicao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao editar o modelo de pneu", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao editar o modelo de pneu, tente novamente");
        }
    }

    @NotNull
    List<PneuModeloListagem> getListagemModelosPneu(@Nullable final Long codEmpresa,
                                                    @Nullable final Long codMarca) {
        try {
            if (codEmpresa == null) {
                // Como a marca é a nível ProLog, a empresa sempre precisa estar presente.
                throw new RuntimeException("codEmpresa não pode ser nulo na busca dos modelos de pneu!");
            }

            return dao.getListagemModelosPneu(codEmpresa, codMarca);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de modelos de pneu:\n"
                    + "codEmpresa: " + codEmpresa + "\n"
                    + "codMarca: " + codMarca, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os modelos de pneu, tente novamente");
        }
    }

    @NotNull
    PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) {
        try {
            return dao.getModeloPneu(codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar modelo de pneu com código: " + codModelo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar modelo de pneu, tente novamente");
        }
    }
}