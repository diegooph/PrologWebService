
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
     * Obtém o valor da propriedade integrarArquivoResult.
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
     * Define o valor da propriedade integrarArquivoResult.
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
