
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
 *         &lt;element name="questionario" type="{http://www.avacorp.com.br/integracaoprolog}AdicionarChecklist" minOccurs="0"/>
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
    "questionario"
})
@XmlRootElement(name = "buscarPerguntasAlternativasQuestionario")
public class BuscarPerguntasAlternativasQuestionario {

    protected AdicionarChecklist questionario;

    /**
     * Obtém o valor da propriedade questionario.
     * 
     * @return
     *     possible object is
     *     {@link AdicionarChecklist }
     *     
     */
    public AdicionarChecklist getQuestionario() {
        return questionario;
    }

    /**
     * Define o valor da propriedade questionario.
     * 
     * @param value
     *     allowed object is
     *     {@link AdicionarChecklist }
     *     
     */
    public void setQuestionario(AdicionarChecklist value) {
        this.questionario = value;
    }

}
