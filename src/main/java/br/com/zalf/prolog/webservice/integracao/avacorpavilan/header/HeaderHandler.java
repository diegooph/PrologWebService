package br.com.zalf.prolog.webservice.integracao.avacorpavilan.header;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by luiz on 24/07/17.
 */
public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {

    public boolean handleMessage(SOAPMessageContext smc) {

        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty) {
            SOAPMessage message = smc.getMessage();
            try {

                SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.addHeader();

                SOAPElement authorization = header.addChildElement("Authorization", "soapenv", null);
                authorization.addTextNode("Basic MDM0NTI2MjUwNjA6MTk5Ni0wOC0wMw==");

                //Print out the outbound SOAP message to System.out
                message.writeTo(System.out);
                System.out.println("");

                message.saveChanges();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                //This handler does nothing with the response from the Web Service so
                //we just print out the SOAP message.
                SOAPMessage message = smc.getMessage();
                message.writeTo(System.out);
                System.out.println("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        return outboundProperty;

    }

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet<QName>();
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return true;
    }

    @Override
    public void close(MessageContext context) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}