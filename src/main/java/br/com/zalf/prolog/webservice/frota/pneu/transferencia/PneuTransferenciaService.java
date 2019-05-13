package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
final class PneuTransferenciaService {
    private static final String TAG = PneuTransferenciaService.class.getSimpleName();
    @NotNull
    private final PneuTransferenciaDao dao = Injection.providePneuTransferenciaDao();

    @NotNull
    ResponseWithCod insertTransferencia(
            final PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Transferência realizada com sucesso",
                    dao.insertTransferencia(pneuTransferenciaRealizacao, false));
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao realizar a transferência", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao realizar a transferência, tente novamente");
        }
    }

    @NotNull
    List<PneuTransferenciaListagem> getListagem(final List<Long> codUnidadesOrigem,
                                                final List<Long> codUnidadesDestino,
                                                final String dataInicial,
                                                final String dataFinal) throws ProLogException {
        try {
            return dao.getListagem(
                    codUnidadesOrigem,
                    codUnidadesDestino,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as transferências", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar as transferências, tente novamente");
        }
    }

    @NotNull
    PneuTransferenciaProcessoVisualizacao getTransferenciaVisualizacao(final Long codTransferencia)
            throws ProLogException {
        try {
            return dao.getVisualizacao(codTransferencia);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar as informações dos pneus transferidos", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar as informações dos pneus transferidos, tente novamente");
        }
    }
}


