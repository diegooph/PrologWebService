
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

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
 *         &lt;element name="farolChecklistResult" type="{http://www.avacorp.com.br/integracaoprologtestes}FarolChecklist" minOccurs="0"/>
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
    "farolChecklistResult"
})
@XmlRootElement(name = "farolChecklistResponse")
public class FarolChecklistResponse {

    protected FarolChecklist2 farolChecklistResult;

    /**
     * Gets the value of the farolChecklistResult property.
     * 
     * @return
     *     possible object is
     *     {@link FarolChecklist2 }
     *     
     */
    public FarolChecklist2 getFarolChecklistResult() {
        return farolChecklistResult;
    }

    /**
     * Sets the value of the farolChecklistResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link FarolChecklist2 }
     *     
     */
    public void setFarolChecklistResult(FarolChecklist2 value) {
        this.farolChecklistResult = value;
    }

}
