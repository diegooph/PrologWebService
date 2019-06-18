package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.headerhandler;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.AutenticacaoWebService;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.ObjectFactory;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created on 06/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SoapHeaderHandler implements SOAPHandler<SOAPMessageContext> {
    @NotNull
    private static final String TAG = SoapHeaderHandler.class.getSimpleName();
    @NotNull
    private final AutenticacaoWebService autenticacaoWebService;

    public SoapHeaderHandler(@NotNull final AutenticacaoWebService autenticacaoWebService) {
        this.autenticacaoWebService = autenticacaoWebService;
    }

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet<>();
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext context) {
        final boolean outboundMessage = (boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        // Caso outboundMessage = false, singnifica que estamos recebendo uma mensagem e nesse caso não queremos
        // tratar nenhum dado.
        if (outboundMessage) {
            try {
                final JAXBElement<AutenticacaoWebService> requesterCredentials =
                        new ObjectFactory().createAutenticacaoWebService(autenticacaoWebService);
                final Marshaller marshaller = JAXBContext.newInstance(AutenticacaoWebService.class).createMarshaller();
                final SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                // Se já possui um Header, então tiremos ele e adicionamos o nosso proprio objeto no Header.
                if (envelope.getHeader() != null) {
                    envelope.getHeader().detachNode();
                }
                final SOAPHeader soapHeader = envelope.addHeader();
                marshaller.marshal(requesterCredentials, soapHeader);
            } catch (JAXBException | SOAPException e) {
                // Podemos, sutilmente, engolir essa exception pois irá resultar em uma erro de autorização e
                // será mapeado pela nosso estrutura posteriormente.
                // Basta logar o erro para saber o que está acontecendo.
                Log.e(TAG, "[ERRO INTEGRAÇÃO]: Erro ao gerar Header com a Autorização", e);
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(final SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(final MessageContext context) {
        // do nothing
    }
}
