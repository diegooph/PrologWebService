
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "AfericaoSoap", targetNamespace = "http://www.avacorp.com.br/integracaoprolog")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface AfericaoAvaCorpAvilanSoap {


    /**
     * 
     * @param medida
     * @return
     *     returns br.com.avacorp.integracaoprolog.IncluirRegistroVeiculo
     */
    @WebMethod(action = "http://www.avacorp.com.br/integracaoprolog/incluirMedida")
    @WebResult(name = "incluirMedidaResult", targetNamespace = "http://www.avacorp.com.br/integracaoprolog")
    @RequestWrapper(localName = "incluirMedida", targetNamespace = "http://www.avacorp.com.br/integracaoprolog", className = "br.com.avacorp.integracaoprolog.IncluirMedida")
    @ResponseWrapper(localName = "incluirMedidaResponse", targetNamespace = "http://www.avacorp.com.br/integracaoprolog", className = "br.com.avacorp.integracaoprolog.IncluirMedidaResponse")
    public IncluirRegistroVeiculo incluirMedida(
            @WebParam(name = "medida", targetNamespace = "http://www.avacorp.com.br/integracaoprolog")
                    IncluirMedida2 medida);

}