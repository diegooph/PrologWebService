package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.IntegracaoOsFilter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanConstants.*;

/**
 * Created on 2020-08-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class IntegracaoOsTask implements Runnable {
    @NotNull
    public static final String TAG = IntegracaoOsTask.class.getSimpleName();
    @NotNull
    private final AvaCorpAvilanRequester requester;
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final List<Long> codsOsSincronizar;

    public IntegracaoOsTask(@NotNull final List<Long> codsOsSincronizar) {
        this.requester = new AvaCorpAvilanRequesterImpl();
        this.integracaoDao = Injection.provideIntegracaoDao();
        this.codsOsSincronizar = codsOsSincronizar;
    }

    @Override
    public void run() {
        if (!codsOsSincronizar.isEmpty()) {
            try {
                final List<OsIntegracao> osSincronizar =
                        integracaoDao.getOrdensServicosIntegracaoByCod(codsOsSincronizar, IntegracaoOsFilter.FECHADAS);
                if (osSincronizar.isEmpty()) {
                    return;
                }
                final ApiAutenticacaoHolder apiAutenticacaoHolder =
                        integracaoDao.getApiAutenticacaoHolder(CODIGO_EMPRESA_AVILAN, SISTEMA_KEY_AVILAN, INSERT_OS);
                enviarOrdensServico(apiAutenticacaoHolder, osSincronizar);
            } catch (final Throwable t) {
                Log.e(TAG, "Erro ao buscar as informações das O.S's no banco de dados", t);
                throw Injection
                        .provideProLogExceptionHandler()
                        .map(t, "Erro ao tentar sincronizar as O.S's");
            }
        }
    }

    private void enviarOrdensServico(@NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
                                     @NotNull final List<OsIntegracao> osSincronizar) {
        for (final OsIntegracao osIntegracao : osSincronizar) {
            try {
                // Envia Ordem de Serviço para o ERP.
                requester.insertChecklistOs(
                        apiAutenticacaoHolder,
                        AvaCorpAvilanConverter.convert(osIntegracao, apiAutenticacaoHolder));
                // Marca Ordem de Serviço como enviada no BD.
                integracaoDao.atualizaStatusOsIntegrada(
                        Collections.singletonList(osIntegracao.getCodInternoOsProlog()),
                        false,
                        false,
                        true);
            } catch (final Throwable t) {
                // Não podemos fazer o throw nesse momento para não travar o fluxo de sincronia.
                try {
                    integracaoDao.logarStatusOsComErro(osIntegracao.getCodInternoOsProlog(), t);
                } catch (final Throwable throwable) {
                    Log.e(TAG,
                            String.format("Erro ao atualizar o status da OS: %s", osIntegracao.getCodOsProlog()),
                            throwable);
                }
                Log.e(TAG, String.format("Erro ao enviar a OS: %s", osIntegracao.getCodOsProlog()), t);
            }
        }
    }
}
