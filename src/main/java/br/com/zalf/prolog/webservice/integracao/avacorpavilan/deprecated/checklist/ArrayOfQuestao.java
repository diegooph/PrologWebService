
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfQuestao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQuestao">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Questao" type="{http://www.avacorp.com.br/integracaoprolog}Questao" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQuestao", propOrder = {
    "questao"
})
public class ArrayOfQuestao {

    @XmlElement(name = "Questao", nillable = true)
    protected List<Questao> questao;

    /**
     * Gets the value of the questao property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the questao property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuestao().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Questao }
     * 
     * 
     */
    public List<Questao> getQuestao() {
        if (questao == null) {
            questao = new ArrayList<Questao>();
        }
        return this.questao;
    }

}
