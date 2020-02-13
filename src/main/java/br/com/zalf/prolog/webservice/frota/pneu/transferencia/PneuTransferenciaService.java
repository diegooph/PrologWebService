package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import br.com.zalf.prolog.webservice.integracao.router.RouterPneuTransferencia;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuTransferenciaService {
    private static final String TAG = PneuTransferenciaService.class.getSimpleName();
    @NotNull
    private final PneuTransferenciaDao dao = Injection.providePneuTransferenciaDao();

    @NotNull
    public ResponseWithCod insertTransferencia(
            @NotNull final String userToken,
            @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao) throws ProLogException {
        try {
            final OffsetDateTime dataHoraSincronizacao = Now.offsetDateTimeUtc();
            return ResponseWithCod.ok(
                    "Transferência realizada com sucesso",
                    RouterPneuTransferencia
                            .create(dao, userToken)
                            .insertTransferencia(
                                    pneuTransferenciaRealizacao,
                                    dataHoraSincronizacao,
                                    false));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar a transferência", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar a transferência, tente novamente");
        }
    }

    @NotNull
    public List<PneuTransferenciaListagem> getListagem(final List<Long> codUnidadesOrigem,
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
    public PneuTransferenciaProcessoVisualizacao getTransferenciaVisualizacao(final Long codTransferencia)
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


