
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Web Service para integracao do Avacorp com o ProLog
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "Checklist", targetNamespace = "http://www.avacorp.com.br/integracaoprolog", wsdlLocation = "http://189.11.175.146/IntegracaoProlog/Checklist.asmx?WSDL")
public class ChecklistAvaCorpAvilanService
    extends Service
{

    private final static URL CHECKLIST_WSDL_LOCATION;
    private final static WebServiceException CHECKLIST_EXCEPTION;
    private final static QName CHECKLIST_QNAME = new QName("http://www.avacorp.com.br/integracaoprolog", "Checklist");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://189.11.175.146/IntegracaoProlog/Checklist.asmx?WSDL");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        CHECKLIST_WSDL_LOCATION = url;
        CHECKLIST_EXCEPTION = e;
    }

    public ChecklistAvaCorpAvilanService() {
        super(__getWsdlLocation(), CHECKLIST_QNAME);
    }

    public ChecklistAvaCorpAvilanService(WebServiceFeature... features) {
        super(__getWsdlLocation(), CHECKLIST_QNAME, features);
    }

    public ChecklistAvaCorpAvilanService(URL wsdlLocation) {
        super(wsdlLocation, CHECKLIST_QNAME);
    }

    public ChecklistAvaCorpAvilanService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, CHECKLIST_QNAME, features);
    }

    public ChecklistAvaCorpAvilanService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ChecklistAvaCorpAvilanService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ChecklistSoap
     */
    @WebEndpoint(name = "ChecklistSoap")
    public ChecklistAvaCorpAvilanSoap getChecklistSoap() {
        return super.getPort(new QName("http://www.avacorp.com.br/integracaoprolog", "ChecklistSoap"), ChecklistAvaCorpAvilanSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ChecklistSoap
     */
    @WebEndpoint(name = "ChecklistSoap")
    public ChecklistAvaCorpAvilanSoap getChecklistSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.avacorp.com.br/integracaoprolog", "ChecklistSoap"), ChecklistAvaCorpAvilanSoap.class, features);
    }

    private static URL __getWsdlLocation() {
        if (CHECKLIST_EXCEPTION!= null) {
            throw CHECKLIST_EXCEPTION;
        }
        return CHECKLIST_WSDL_LOCATION;
    }

}
