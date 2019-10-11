package br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.banda.model.PneuModeloBandaEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuMarcaModelo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.modelo.model.PneuModeloVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuModeloService {
    private static final String TAG = PneuModeloBandaEdicao.class.getSimpleName();
    @NotNull
    private final PneuModeloDao dao = Injection.providePneuModeloDao();


    public List<PneuMarcaModelo> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws ProLogException {
        try {
            return dao.getMarcaModeloPneuByCodEmpresa(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar as marcas de pneu da empresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar as marcas de pneu, tente novamente");
        }
    }

    public AbstractResponse insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) throws ProLogException {
        try {
            return ResponseWithCod.ok("Modelo inserido com sucesso", dao.insertModeloPneu(pneuModeloInsercao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de pneu. Empresa: " + pneuModeloInsercao.getCodEmpresa() + " Marca: " + pneuModeloInsercao.getCodMarca(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir modelo de pneu, tente novamente");
        }
    }

    public ResponseWithCod updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao)
            throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Modelo de Pneu editado com sucesso",
                    dao.updateModeloPneu(pneuModeloEdicao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao editar o modelo de pneu", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao editar o modelo de pneu, tente novamente");
        }
    }

    public PneuModeloVisualizacao getModeloPneu(Long codModelo) throws ProLogException {
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
