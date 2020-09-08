
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfQuestionarioVeiculos complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQuestionarioVeiculos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QuestionarioVeiculos" type="{http://www.avacorp.com.br/integracaoprolog}QuestionarioVeiculos" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQuestionarioVeiculos", propOrder = {
    "questionarioVeiculos"
})
public class ArrayOfQuestionarioVeiculos {

    @XmlElement(name = "QuestionarioVeiculos", nillable = true)
    protected List<QuestionarioVeiculos> questionarioVeiculos;

    /**
     * Gets the value of the questionarioVeiculos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the questionarioVeiculos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuestionarioVeiculos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuestionarioVeiculos }
     * 
     * 
     */
    public List<QuestionarioVeiculos> getQuestionarioVeiculos() {
        if (questionarioVeiculos == null) {
            questionarioVeiculos = new ArrayList<QuestionarioVeiculos>();
        }
        return this.questionarioVeiculos;
    }

}
