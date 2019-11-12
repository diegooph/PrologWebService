package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.error.ProLogError;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.RetornoOsCorretivaVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.headerhandler.SoapHeaderHandler;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester.ManutencaoWSTerceiros;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester.ManutencaoWSTerceirosSoap;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.List;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturRequesterImpl implements GlobusPiccoloturRequester {
    @NotNull
    private static final String TAG = GlobusPiccoloturRequesterImpl.class.getSimpleName();

    @NotNull
    @Override
    public Long insertItensNok(@NotNull final OrdemDeServicoCorretivaPrologVO ordemDeServicoCorretivaPrologVO) {
        if (BuildConfig.DEBUG) {
            System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        }
        // Utilizamos este try/catch para lançar um erro da integração para qualquer coisa que acontecer que não for
        // algo já mapeado pelo ProLog.
        // Com essa verificação, o usuário sempre receberá um erro personalizado informando que o erro que está
        // acontecendo é devido à integração e não algo interno do ProLog.
        try {
            return handleXmlResponse(
                    getSoapRequester().gerarOrdemDeServicoCorretivaProlog(ordemDeServicoCorretivaPrologVO));
        } catch (final Throwable t) {
            if (!(t instanceof GlobusPiccoloturException)) {
                throw new GlobusPiccoloturException(
                        "[ERRO INTEGRAÇÃO]: Erro na comunicação com o Sistema Globus",
                        "Uma exception não mapeada estourou na integração, a exception está presente nos logs",
                        t);
            }
            throw t;
        }
    }

    @NotNull
    @Override
    public SuccessResponseIntegracao insertProcessoMovimentacao(
            @NotNull final String url,
            @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus) throws Throwable {
        final GlobusPiccoloturRest service = GlobusPiccoloturRestClient.getService(GlobusPiccoloturRest.class);
        final Call<SuccessResponseIntegracao> call =
                service.insertProcessoMovimentacao(url, processoMovimentacaoGlobus);
        return handleJsonResponse(call.execute());
    }

    @NotNull
    private <T> T handleJsonResponse(@Nullable final Response<T> response) throws Throwable {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new GlobusPiccoloturException("[INTEGRAÇÃO] Erro ao movimentar pneus no sistema integrado");
                }
                throw GlobusPiccoloturException.from(toProLogError(response.errorBody()));
            }
        } else {
            throw new GlobusPiccoloturException("[INTEGRAÇÃO] Erro ao movimentar pneus no sistema integrado");
        }
    }

    @NotNull
    private ProLogError toProLogError(@NotNull final ResponseBody errorBody) throws Throwable {
        final String jsonBody = errorBody.string();
        try {
            return ProLogError.generateFromString(jsonBody);
        } catch (final Throwable t) {
            final String msg = String.format("Erro ao realizar o parse da mensagem de erro recebida da integração:\n" +
                    "jsonBody: %s", jsonBody);
            Log.e(TAG, msg, t);
            throw new GlobusPiccoloturException("[INTEGRAÇÃO] Erro ao movimentar pneus no sistema integrado");
        }
    }

    @NotNull
    private Long handleXmlResponse(@Nullable final RetornoOsCorretivaVO result) {
        if (result != null) {
            if (result.isSucesso()) {
                return (long) result.getCodigoOS();
            } else {
                throw new GlobusPiccoloturException("[ERRO INTEGRAÇÃO]: " + result.getMensagemDeRetorno());
            }
        } else {
            throw new GlobusPiccoloturException("[ERRO INTEGRAÇÃO]: Nenhuma informação retornada pelo Globus");
        }
    }

    @NotNull
    private ManutencaoWSTerceirosSoap getSoapRequester() {
        final ManutencaoWSTerceirosSoap soap = new ManutencaoWSTerceiros().getManutencaoWSTerceirosSoap();
        final Binding binding = ((BindingProvider) soap).getBinding();
        final List<Handler> handlerChain = binding.getHandlerChain();
        handlerChain.add(new SoapHeaderHandler(GlobusPiccoloturAutenticacaoCreator.createCredentials()));
        binding.setHandlerChain(handlerChain);
        return soap;
    }
}
