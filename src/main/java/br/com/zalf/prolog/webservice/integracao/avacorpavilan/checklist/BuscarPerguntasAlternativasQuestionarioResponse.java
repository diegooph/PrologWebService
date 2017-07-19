
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
 *         &lt;element name="buscarPerguntasAlternativasQuestionarioResult" type="{http://www.avacorp.com.br/integracaoprolog}PerguntasAlternativasQuestionario" minOccurs="0"/>
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
    "buscarPerguntasAlternativasQuestionarioResult"
})
@XmlRootElement(name = "buscarPerguntasAlternativasQuestionarioResponse")
public class BuscarPerguntasAlternativasQuestionarioResponse {

    protected PerguntasAlternativasQuestionario buscarPerguntasAlternativasQuestionarioResult;

    /**
     * Gets the value of the buscarPerguntasAlternativasQuestionarioResult property.
     * 
     * @return
     *     possible object is
     *     {@link PerguntasAlternativasQuestionario }
     *     
     */
    public PerguntasAlternativasQuestionario getBuscarPerguntasAlternativasQuestionarioResult() {
        return buscarPerguntasAlternativasQuestionarioResult;
    }

    /**
     * Sets the value of the buscarPerguntasAlternativasQuestionarioResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link PerguntasAlternativasQuestionario }
     *     
     */
    public void setBuscarPerguntasAlternativasQuestionarioResult(PerguntasAlternativasQuestionario value) {
        this.buscarPerguntasAlternativasQuestionarioResult = value;
    }

}
