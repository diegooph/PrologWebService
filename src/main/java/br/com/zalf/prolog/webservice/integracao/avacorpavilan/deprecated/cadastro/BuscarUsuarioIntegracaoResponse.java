
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the buscarUsuarioIntegracaoResult property.
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
     * Sets the value of the buscarUsuarioIntegracaoResult property.
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
