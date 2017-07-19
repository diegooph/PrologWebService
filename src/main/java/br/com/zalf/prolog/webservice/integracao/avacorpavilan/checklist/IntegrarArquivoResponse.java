
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
 *         &lt;element name="integrarArquivoResult" type="{http://www.avacorp.com.br/integracaoprolog}RetornoPadrao" minOccurs="0"/>
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
    "integrarArquivoResult"
})
@XmlRootElement(name = "integrarArquivoResponse")
public class IntegrarArquivoResponse {

    protected RetornoPadrao integrarArquivoResult;

    /**
     * Gets the value of the integrarArquivoResult property.
     * 
     * @return
     *     possible object is
     *     {@link RetornoPadrao }
     *     
     */
    public RetornoPadrao getIntegrarArquivoResult() {
        return integrarArquivoResult;
    }

    /**
     * Sets the value of the integrarArquivoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetornoPadrao }
     *     
     */
    public void setIntegrarArquivoResult(RetornoPadrao value) {
        this.integrarArquivoResult = value;
    }

}
