package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.RetornoOsCorretivaVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.headerhandler.SoapHeaderHandler;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester.ManutencaoWSTerceiros;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester.ManutencaoWSTerceirosSoap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            return handleResponse(
                    getSoapRequester().gerarOrdemDeServicoCorretivaProlog(ordemDeServicoCorretivaPrologVO));
        } catch (final Throwable t) {
            if (!(t instanceof GlobusPiccoloturException)) {
                throw new GlobusPiccoloturException("[ERRO INTEGRAÇÃO]: Erro na comunicação com o Globus");
            }
            throw t;
        }
    }

    @NotNull
    private Long handleResponse(@Nullable final RetornoOsCorretivaVO result) {
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
