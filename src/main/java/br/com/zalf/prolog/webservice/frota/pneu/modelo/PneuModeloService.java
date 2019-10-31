package br.com.zalf.prolog.webservice.frota.pneu.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.PneuModeloBandaEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloListagem;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.PneuModeloVisualizacao;
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


    public ResponseWithCod insertModeloPneu(@NotNull final PneuModeloInsercao pneuModeloInsercao) {
        try {
            return ResponseWithCod.ok("Modelo inserido com sucesso", dao.insertModeloPneu(pneuModeloInsercao));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de pneu. Empresa: " + pneuModeloInsercao.getCodEmpresa() +
                    " Marca: " + pneuModeloInsercao.getCodMarca(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o modelo de pneu, tente novamente");
        }
    }

    public ResponseWithCod updateModeloPneu(@NotNull final PneuModeloEdicao pneuModeloEdicao) {
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

    public List<PneuModeloListagem> getListagemMarcasModelosPneu(@NotNull final Long codEmpresa) {
        try {
            return dao.getListagemMarcasModelosPneu(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar as marcas de pneu da empresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar as marcas de pneu, tente novamente");
        }
    }

    public PneuModeloVisualizacao getModeloPneu(@NotNull final Long codModelo) {
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