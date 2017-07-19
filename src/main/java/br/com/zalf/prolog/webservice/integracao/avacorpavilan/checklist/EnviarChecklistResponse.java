
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

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
 *         &lt;element name="enviarChecklistResult" type="{http://www.avacorp.com.br/integracaoprolog}EnviaRespostaAvaliacao" minOccurs="0"/>
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
    "enviarChecklistResult"
})
@XmlRootElement(name = "enviarChecklistResponse")
public class EnviarChecklistResponse {

    protected EnviaRespostaAvaliacao enviarChecklistResult;

    /**
     * Gets the value of the enviarChecklistResult property.
     * 
     * @return
     *     possible object is
     *     {@link EnviaRespostaAvaliacao }
     *     
     */
    public EnviaRespostaAvaliacao getEnviarChecklistResult() {
        return enviarChecklistResult;
    }

    /**
     * Sets the value of the enviarChecklistResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnviaRespostaAvaliacao }
     *     
     */
    public void setEnviarChecklistResult(EnviaRespostaAvaliacao value) {
        this.enviarChecklistResult = value;
    }

}
