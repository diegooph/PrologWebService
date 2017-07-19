
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
 *         &lt;element name="respostas" type="{http://www.avacorp.com.br/integracaoprolog}RespostasAvaliacao" minOccurs="0"/>
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
    "respostas"
})
@XmlRootElement(name = "enviarChecklist")
public class EnviarChecklist {

    protected RespostasAvaliacao respostas;

    /**
     * Gets the value of the respostas property.
     * 
     * @return
     *     possible object is
     *     {@link RespostasAvaliacao }
     *     
     */
    public RespostasAvaliacao getRespostas() {
        return respostas;
    }

    /**
     * Sets the value of the respostas property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespostasAvaliacao }
     *     
     */
    public void setRespostas(RespostasAvaliacao value) {
        this.respostas = value;
    }

}
