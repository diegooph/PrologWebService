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

    public List<PneuMarcaBandas> getMarcaModeloBanda(Long codEmpresa) throws ProLogException {
        try {
            return dao.getMarcaModeloBanda(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marcas de banda da empresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcas de banda");
        }
    }

    public AbstractResponse insertMarcaBanda(PneuMarcaBandas marca, Long codEmpresa) throws ProLogException {
        try {
            return ResponseWithCod.ok("Marca inserida com sucesso", dao.insertMarcaBanda(marca, codEmpresa));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir marca de banda para empresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir a marca da banda");
        }
    }

    public boolean updateMarcaBanda(PneuMarcaBandas marca, Long codEmpresa) throws ProLogException {
        try {
            return dao.updateMarcaBanda(marca, codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar marca de banda da empresa: " + codEmpresa, t);
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
