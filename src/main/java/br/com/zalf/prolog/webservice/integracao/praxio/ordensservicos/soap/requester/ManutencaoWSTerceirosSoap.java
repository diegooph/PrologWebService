
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.requester;

import br.com.zalf.prolog.webservice.integracao.praxio.GlobusPiccoloturConstants;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.ObjectFactory;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.RetornoOsCorretivaVO;

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
 */
@WebService(
        name = "ManutencaoWSTerceirosSoap",
        targetNamespace = GlobusPiccoloturConstants.NAMESPACE)
@XmlSeeAlso({ObjectFactory.class})
public interface ManutencaoWSTerceirosSoap {

    /**
     * Gerar ordem de serviço corretiva por CheckList Prolog.
     *
     * @param ordemDeServico Objeto contendo as tags que serão recebidas pelo sistema integrado.
     * @return returns br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.RetornoOsCorretivaVO
     */
    @WebMethod(
            operationName = "GerarOrdemDeServicoCorretivaProlog",
            action = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/GerarOrdemDeServicoCorretivaProlog")
    @WebResult(
            name = "GerarOrdemDeServicoCorretivaPrologResult",
            targetNamespace = GlobusPiccoloturConstants.NAMESPACE)
    @RequestWrapper(
            localName = "GerarOrdemDeServicoCorretivaProlog",
            targetNamespace = GlobusPiccoloturConstants.NAMESPACE,
            className = "br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.GerarOrdemDeServicoCorretivaProlog")
    @ResponseWrapper(
            localName = "GerarOrdemDeServicoCorretivaPrologResponse",
            targetNamespace = GlobusPiccoloturConstants.NAMESPACE,
            className = "br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.GerarOrdemDeServicoCorretivaPrologResponse")
    RetornoOsCorretivaVO gerarOrdemDeServicoCorretivaProlog(
            @WebParam(
                    name = "ordemDeServico",
                    targetNamespace = GlobusPiccoloturConstants.NAMESPACE)
                    OrdemDeServicoCorretivaPrologVO ordemDeServico);
}
