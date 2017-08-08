
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

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
@WebServiceClient(name = "Cadastro", targetNamespace = "http://www.avacorp.com.br/integracaoprolog", wsdlLocation = "http://189.11.175.146/IntegracaoProlog/Cadastro.asmx?WSDL")
public class CadastroAvaCorpAvilanService
    extends Service
{

    private final static URL CADASTRO_WSDL_LOCATION;
    private final static WebServiceException CADASTRO_EXCEPTION;
    private final static QName CADASTRO_QNAME = new QName("http://www.avacorp.com.br/integracaoprolog", "Cadastro");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://189.11.175.146/IntegracaoProlog/Cadastro.asmx?WSDL");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        CADASTRO_WSDL_LOCATION = url;
        CADASTRO_EXCEPTION = e;
    }

    public CadastroAvaCorpAvilanService() {
        super(__getWsdlLocation(), CADASTRO_QNAME);
    }

    public CadastroAvaCorpAvilanService(WebServiceFeature... features) {
        super(__getWsdlLocation(), CADASTRO_QNAME, features);
    }

    public CadastroAvaCorpAvilanService(URL wsdlLocation) {
        super(wsdlLocation, CADASTRO_QNAME);
    }

    public CadastroAvaCorpAvilanService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, CADASTRO_QNAME, features);
    }

    public CadastroAvaCorpAvilanService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CadastroAvaCorpAvilanService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns CadastroSoap
     */
    @WebEndpoint(name = "CadastroSoap")
    public CadastroAvaCorpAvilanSoap getCadastroSoap() {
        return super.getPort(new QName("http://www.avacorp.com.br/integracaoprolog", "CadastroSoap"), CadastroAvaCorpAvilanSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CadastroSoap
     */
    @WebEndpoint(name = "CadastroSoap")
    public CadastroAvaCorpAvilanSoap getCadastroSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.avacorp.com.br/integracaoprolog", "CadastroSoap"), CadastroAvaCorpAvilanSoap.class, features);
    }

    private static URL __getWsdlLocation() {
        if (CADASTRO_EXCEPTION!= null) {
            throw CADASTRO_EXCEPTION;
        }
        return CADASTRO_WSDL_LOCATION;
    }

}
