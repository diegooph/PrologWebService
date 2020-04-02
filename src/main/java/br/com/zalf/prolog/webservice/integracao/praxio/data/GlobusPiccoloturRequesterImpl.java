package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimento;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimentoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.RetornoOsCorretivaVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.headerhandler.SoapHeaderHandler;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester.ManutencaoWSTerceiros;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester.ManutencaoWSTerceirosSoap;
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
    private static final String ERRO_AO_AUTENTICAR_INTEGRACAO = "[INTEGRAÇÃO] Erro ao autenticar integração";
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
    public GlobusPiccoloturAutenticacaoResponse getTokenAutenticacaoIntegracao(
            @NotNull final String url,
            @NotNull final String token,
            @NotNull final Long shortCode) throws Throwable {
        final GlobusPiccoloturRest service = GlobusPiccoloturRestClient.getService(GlobusPiccoloturRest.class);
        final Call<GlobusPiccoloturAutenticacaoResponse> call =
                service.getTokenAutenticacaoIntegracao(url, token, shortCode);
        return handleJsonResponse(call.execute(), true);
    }

    @NotNull
    @Override
    public GlobusPiccoloturMovimentacaoResponse insertProcessoMovimentacao(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus) throws Throwable {
        final GlobusPiccoloturRest service = GlobusPiccoloturRestClient.getService(GlobusPiccoloturRest.class);
        final Call<GlobusPiccoloturMovimentacaoResponse> call =
                service.insertProcessoMovimentacao(url, tokenIntegracao, processoMovimentacaoGlobus);
        return handleJsonResponse(call.execute());
    }

    @NotNull
    @Override
    public GlobusPiccoloturLocalMovimentoResponse getLocaisMovimentoGlobusResponse(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final String cpfColaborador) throws Throwable {
        final GlobusPiccoloturRest service = GlobusPiccoloturRestClient.getService(GlobusPiccoloturRest.class);
        final Call<GlobusPiccoloturLocalMovimentoResponse> call =
                service.getLocaisMovimentoGlobus(url, tokenIntegracao, cpfColaborador);
        final GlobusPiccoloturLocalMovimentoResponse response = handleJsonResponse(call.execute());
        if (!response.isSucesso() || response.getLocais() == null || response.getUsuarioGlobus() == null) {
            throw new GlobusPiccoloturException("[INTEGRAÇÂO] Erro ao buscar Locais de Movimento para a movimentação");
        }
        return response;
    }

    @NotNull
    @Override
    public List<GlobusPiccoloturLocalMovimento> getLocaisMovimentoGlobus(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final String cpfColaborador) throws Throwable {
        final GlobusPiccoloturRest service = GlobusPiccoloturRestClient.getService(GlobusPiccoloturRest.class);
        final Call<GlobusPiccoloturLocalMovimentoResponse> call =
                service.getLocaisMovimentoGlobus(url, tokenIntegracao, cpfColaborador);
        final GlobusPiccoloturLocalMovimentoResponse response = handleJsonResponse(call.execute());
        if (!response.isSucesso() || response.getLocais() == null) {
            throw new GlobusPiccoloturException("[INTEGRAÇÂO] Erro ao buscar Locais de Movimento para a movimentação");
        }
        return response.getLocais();
    }

    @NotNull
    private <T> T handleJsonResponse(@Nullable final Response<T> response) throws Throwable {
        return handleJsonResponse(response, false);
    }

    @NotNull
    private <T> T handleJsonResponse(@Nullable final Response<T> response,
                                     final boolean tokenResponse) throws Throwable {
        if (response != null) {
            if (response.isSuccessful() && response.body() != null) {
                // A autenticação retorna status = 200 inclusive quando for um erro, precisamos tratar os cenários de
                // erro utilizando o body, para essas situações.
                if (tokenResponse) {
                    try {
                        final GlobusPiccoloturAutenticacaoResponse atenticacaoResponse =
                                ((GlobusPiccoloturAutenticacaoResponse) response.body());
                        if (atenticacaoResponse.isSucesso()) {
                            return response.body();
                        }
                        throw new GlobusPiccoloturException(
                                ERRO_AO_AUTENTICAR_INTEGRACAO,
                                "O método de autenticação retornou um erro, devemos tratar imediatamente",
                                // Lançamos uma exception contendo o conteúdo do erro recebido, assim podemos logar essa
                                // informação facilitando a correção de erros.
                                new Exception(atenticacaoResponse.getData()));
                    } catch (final ClassCastException c) {
                        throw new GlobusPiccoloturException(
                                ERRO_AO_AUTENTICAR_INTEGRACAO,
                                "O retorno obtido da integração não está no padrão esperado",
                                c);
                    }
                }
                return response.body();
            } else {
                if (response.errorBody() == null) {
                    throw new GlobusPiccoloturException("[INTEGRAÇÃO] Nenhuma resposta obtida do Globus");
                }
                // Tratamos de forma específica o retorno de erro da requisição de autenticação.
                if (tokenResponse) {
                    throw new GlobusPiccoloturException(ERRO_AO_AUTENTICAR_INTEGRACAO);
                }
                throw GlobusPiccoloturException.from(toGlobusPiccoloturResponse(response.errorBody()));
            }
        } else {
            throw new GlobusPiccoloturException("[INTEGRAÇÃO] Nenhuma resposta obtida do Globus");
        }
    }

    @NotNull
    private GlobusPiccoloturMovimentacaoResponse toGlobusPiccoloturResponse(
            @NotNull final ResponseBody errorBody) throws Throwable {
        final String jsonBody = errorBody.string();
        try {
            return GlobusPiccoloturMovimentacaoResponse.generateFromString(jsonBody);
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
