package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.AutenticacaoWebService;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.ObjectFactory;

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
public class SoapHeaderHandler implements SOAPHandler<SOAPMessageContext> {
    private static final String TAG = SoapHeaderHandler.class.getSimpleName();
    private final AutenticacaoWebService autenticacaoWebService;

    public SoapHeaderHandler(final AutenticacaoWebService autenticacaoWebService) {
        this.autenticacaoWebService = autenticacaoWebService;
    }

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet<>();
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext context) {
        final boolean out = (boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (out) {
            try {
                // bookmark #1 - please read explanation after code
                final ObjectFactory objectFactory = new ObjectFactory();
                // creating JAXBElement from headerObj
                final JAXBElement<AutenticacaoWebService> requesterCredentials =
                        objectFactory.createAutenticacaoWebService(autenticacaoWebService);

                // obtaining marshaller which should marshal instance to xml
                final Marshaller marshaller = JAXBContext.newInstance(AutenticacaoWebService.class).createMarshaller();

                final SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();

                if (envelope.getHeader() != null) {
                    envelope.getHeader().detachNode();
                }
                // adding header because otherwise it's null
                final SOAPHeader soapHeader = envelope.addHeader();
                // marshalling instance (appending) to SOAP header's xml node
                marshaller.marshal(requesterCredentials, soapHeader);
            } catch (JAXBException | SOAPException e) {
                Log.e(TAG,"ERRO ao gerar Header", e);
            }
        } else {
            // inbound - do nothing.
        }
        return true;
    }

    @Override
    public boolean handleFault(final SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(final MessageContext context) {

    }
}
