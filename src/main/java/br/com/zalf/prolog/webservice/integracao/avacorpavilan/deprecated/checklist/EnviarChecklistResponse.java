
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

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
     * Obtém o valor da propriedade enviarChecklistResult.
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
     * Define o valor da propriedade enviarChecklistResult.
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
