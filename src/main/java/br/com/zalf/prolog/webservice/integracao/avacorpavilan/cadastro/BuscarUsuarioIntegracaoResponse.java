
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="buscarUsuarioIntegracaoResult" type="{http://www.avacorp.com.br/integracaoprolog}UsuarioIntegracao" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "buscarUsuarioIntegracaoResult"
})
@XmlRootElement(name = "buscarUsuarioIntegracaoResponse")
public class BuscarUsuarioIntegracaoResponse {

    protected UsuarioIntegracao buscarUsuarioIntegracaoResult;

    /**
     * Obtém o valor da propriedade buscarUsuarioIntegracaoResult.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioIntegracao }
     *     
     */
    public UsuarioIntegracao getBuscarUsuarioIntegracaoResult() {
        return buscarUsuarioIntegracaoResult;
    }

    /**
     * Define o valor da propriedade buscarUsuarioIntegracaoResult.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioIntegracao }
     *     
     */
    public void setBuscarUsuarioIntegracaoResult(UsuarioIntegracao value) {
        this.buscarUsuarioIntegracaoResult = value;
    }

}
