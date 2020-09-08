
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
     * Obtém o valor da propriedade buscarPerguntasAlternativasQuestionarioResult.
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
     * Define o valor da propriedade buscarPerguntasAlternativasQuestionarioResult.
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
