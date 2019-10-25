package br.com.zalf.prolog.webservice.frota.pneu.pneu.banda;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.*;

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


    public List<PneuMarcaBanda> listagemMarcasBandas(@NotNull final Long codEmpresa) throws ProLogException {
        try {
            return dao.listagemMarcasBandas(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de marcas de banda: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar listagem de marcas de banda");
        }
    }

    public PneuMarcaBanda getMarcaBanda(@NotNull final Long codMarca) throws ProLogException {
        try {
            return dao.getMarcaBanda(codMarca);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marca de banda: " + codMarca, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar de marca de banda");
        }
    }

    public List<PneuMarcaModelosBanda> listagemMarcasModelosBandas(@NotNull final Long codEmpresa) throws ProLogException {
        try {
            return dao.listagemMarcasModelosBandas(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar listagem de marcas e modelos de banda: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar listagem de marcas e modelos de banda");
        }
    }

    public PneuMarcaModeloBanda getMarcaModeloBanda(@NotNull final Long codModelo) throws ProLogException {
        try {
            return dao.getMarcaModeloBanda(codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marca e modelo de banda da empresa: " + codModelo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marca e modelo de banda");
        }


    }

    public AbstractResponse insertMarcaBanda(@NotNull final PneuMarcaModelosBanda marca,
                                             @NotNull final Long codEmpresa) throws ProLogException {
        try {
            return ResponseWithCod.ok("Marca inserida com sucesso", dao.insertMarcaBanda(marca, codEmpresa));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir marca de banda para empresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir a marca da banda");
        }
    }

    public boolean updateMarcaBanda(@NotNull final PneuMarcaBanda marcaBanda) throws ProLogException {
        try {
            return dao.updateMarcaBanda(marcaBanda);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar marca de banda da empresa: " + marcaBanda.getCodigo(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar marca de banda");
        }
    }

    public AbstractResponse insertModeloBanda(@NotNull final PneuModeloBandaInsercao pneuModeloBandaInsercao)
            throws ProLogException {
        try {
            return ResponseWithCod.ok("Modelo inserido com sucesso",
                    dao.insertModeloBanda(pneuModeloBandaInsercao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de banda para marca: " + pneuModeloBandaInsercao.getCodMarca() +
                    " Empresa: " + pneuModeloBandaInsercao.getCodEmpresa(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir modelo de banda");
        }
    }

    public ResponseWithCod updateModeloBanda(@NotNull final PneuModeloBandaEdicao pneuModeloBandaEdicao)
            throws ProLogException {
        try {
            return ResponseWithCod.ok("Modelo de Banda editado com sucesso",
                    dao.updateModeloBanda(pneuModeloBandaEdicao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao editar a banda", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao editar a banda, tente novamente");
        }
    }
}