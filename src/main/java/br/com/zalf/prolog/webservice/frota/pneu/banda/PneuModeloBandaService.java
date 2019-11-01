package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloBandaService {
    private static final String TAG = PneuModeloBandaEdicao.class.getSimpleName();
    @NotNull
    private final PneuModeloBandaDao dao = Injection.providePneuModeloBandaDao();

    public AbstractResponse insertMarcaBanda(@NotNull final PneuMarcaBandaInsercao marcaBanda) {
        try {
            return ResponseWithCod.ok("Marca de banda inserida com sucesso", dao.insertMarcaBanda(marcaBanda));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir marca de banda para empresa: " + marcaBanda.getCodEmpresa(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir marca da banda, tente novamente");
        }
    }

    public ResponseWithCod updateMarcaBanda(@NotNull final PneuMarcaBandaEdicao marcaBanda) {
        try {
            return ResponseWithCod.ok("Marca de banda editada com sucesso", dao.updateMarcaBanda(marcaBanda));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar marca de banda da empresa: " + marcaBanda.getCodigo(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar marca de banda, tente novamente");
        }
    }

    public List<PneuMarcaBandaListagemVisualizacao> getListagemMarcasBandas(@NotNull final Long codEmpresa) {
        try {
            return dao.getListagemMarcasBandas(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de marcas de banda: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcas de banda, tente novamente");
        }
    }

    public PneuMarcaBandaListagemVisualizacao getMarcaBanda(@NotNull final Long codMarca) {
        try {
            return dao.getMarcaBanda(codMarca);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marca de banda: " + codMarca, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marca de banda, tente novamente");
        }
    }

    public AbstractResponse insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao) {
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

    public ResponseWithCod updateModeloBanda(@NotNull final PneuModeloBandaEdicao pneuModeloBandaEdicao) {
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

    public List<PneuModeloBandaListagem> getListagemMarcasModelosBandas(@NotNull final Long codEmpresa) {
        try {
            return dao.getListagemMarcasModelosBandas(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de marcas e modelos de banda: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os modelos de banda, tente novamente");
        }
    }

    public PneuModeloBandaVisualizacao getMarcaModeloBanda(@NotNull final Long codModelo) {
        try {
            return dao.getMarcaModeloBanda(codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marca e modelo de banda da empresa: " + codModelo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar modelo de banda, tente novamente");
        }
    }
}