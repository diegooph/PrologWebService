
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.farol;

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
 *         &lt;element name="farol" type="{http://www.avacorp.com.br/integracaoprolog}Farol" minOccurs="0"/>
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
    "farol"
})
@XmlRootElement(name = "farolChecklist")
public class FarolChecklist {

    protected Farol farol;

    /**
     * Gets the value of the farol property.
     * 
     * @return
     *     possible object is
     *     {@link Farol }
     *     
     */
    public Farol getFarol() {
        return farol;
    }

    /**
     * Sets the value of the farol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Farol }
     *     
     */
    public void setFarol(Farol value) {
        this.farol = value;
    }

}
