package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.headerhandler;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologUtils;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.AutenticacaoWebService;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.ObjectFactory;
import com.sun.xml.bind.v2.ContextFactory;
import jakarta.xml.soap.*;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
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
        logMessage(context, "SOAP Message is : ");
        final boolean outboundMessage = (boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        // Caso outboundMessage = false, singnifica que estamos recebendo uma mensagem e nesse caso não queremos
        // tratar nenhum dado.
        if (outboundMessage) {
            try {
                final JAXBElement<AutenticacaoWebService> requesterCredentials =
                        new ObjectFactory().createAutenticacaoWebService(autenticacaoWebService);
                // Temos que usar com.sun.xml.internal.bind.v2.ContextFactory, obrigatoriamente, pois ele consegue
                // criar corretamente o contexto para marshallar o objeto.
                final Marshaller marshaller =
                        ContextFactory.createContext(new Class[]{AutenticacaoWebService.class},
                                                     new HashMap<>()).createMarshaller();
                final SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                // Se já possui um Header, então tiremos ele e adicionamos o nosso proprio objeto no Header.
                if (envelope.getHeader() != null) {
                    envelope.getHeader().detachNode();
                }
                final SOAPHeader soapHeader = envelope.addHeader();
                marshaller.marshal(requesterCredentials, soapHeader);
            } catch (final JAXBException | SOAPException e) {
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
        logMessage(context, "SOAP Error is : ");
        return false;
    }

    @Override
    public void close(final MessageContext context) {
        // Do nothing.
    }

    private boolean logMessage(final MessageContext mc, final String type) {
        try {
            if (PrologUtils.isDebug()) {
                Log.d(TAG, type);
                final SOAPMessage msg = ((SOAPMessageContext) mc).getMessage();

                // Print out the Mime Headers
                final MimeHeaders mimeHeaders = msg.getMimeHeaders();
                final Iterator mhIterator = mimeHeaders.getAllHeaders();
                MimeHeader mh;
                String header;
                Log.d(TAG, "  Mime Headers:");
                while (mhIterator.hasNext()) {
                    mh = (MimeHeader) mhIterator.next();
                    header = new StringBuffer(" Name: ")
                            .append(mh.getName()).append(" Value: ")
                            .append(mh.getValue()).toString();
                    Log.d(TAG, header);
                }

                Log.d(TAG, " SOAP Message: ");
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                msg.writeTo(baos);
                Log.d(TAG, "   " + baos.toString());
                baos.close();
            }

            return true;
        } catch (final Exception e) {
            Log.e(TAG, "Error logging SOAP message", e);
            return false;
        }
    }
}
