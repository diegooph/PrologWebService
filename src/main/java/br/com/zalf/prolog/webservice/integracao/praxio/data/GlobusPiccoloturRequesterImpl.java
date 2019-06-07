package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.List;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class GlobusPiccoloturRequesterImpl implements GlobusPiccoloturRequester {
    @Override
    public Long insertItensNok(
            @NotNull final OrdemDeServicoCorretivaPrologVO ordemDeServicoCorretivaPrologVO) throws Throwable {
        if (BuildConfig.DEBUG) {
            System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
            System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
            System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        }

        final AutenticacaoWebService autenticacaoWebService = new AutenticacaoWebService();
        autenticacaoWebService.setToken("OTkxMDEwOzMyNzs1MTA");
        autenticacaoWebService.setShortCode(1032);
        autenticacaoWebService.setNomeMetodo("GerarOrdemDeServicoCorretivaProlog");

        final ManutencaoWSTerceiros service = new ManutencaoWSTerceiros();
        final ManutencaoWSTerceirosSoap soap = service.getManutencaoWSTerceirosSoap();

        final Binding binding = ((BindingProvider) soap).getBinding();
        final List<Handler> handlerChain = binding.getHandlerChain();
        handlerChain.add(new SoapHeaderHandler(autenticacaoWebService));
        binding.setHandlerChain(handlerChain);

        final RetornoOsCorretivaVO retornoOsCorretivaVO =
                soap.gerarOrdemDeServicoCorretivaProlog(ordemDeServicoCorretivaPrologVO);

        if (!retornoOsCorretivaVO.isSucesso()) {
            throw new GenericException(retornoOsCorretivaVO.getMensagemDeRetorno());
        }
        return (long) retornoOsCorretivaVO.getCodigoOS();
    }
}
