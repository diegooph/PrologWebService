package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaService {
    private static final String TAG = PneuTransferenciaService.class.getSimpleName();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();
    private final PneuTransferenciaDao dao = Injection.providePneuTransferenciaDao();

    public List<PneuTransferenciaListagem> transferenciaListagem(@NotNull final List<Long> codUnidadesOrigem,
                                                                 @NotNull final List<Long> codUnidadesDestino,
                                                                 @NotNull final String dataInicial,
                                                                 @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getListagem(
                    codUnidadesOrigem,
                    codUnidadesDestino,
                    dataInicial,
                    dataFinal);

        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as transferências", e);
            throw exceptionHandler.map(e, "Erro ao buscar as transferências");
        }
    }

    public List<PneuTransferenciaProcessoVisualizacao> transferenciaVisualizacao(@NotNull final Long codTransferencia)
            throws ProLogException{
        try {
            return dao.getVisualizacao(
                    codTransferencia);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as informações dos pneus transferidos", e);
            throw exceptionHandler.map(e, "Erro ao buscar as informações dos pneus transferidos");
        }
    }
}
