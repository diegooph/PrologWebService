
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfAvaliacao complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAvaliacao">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Avaliacao" type="{http://www.avacorp.com.br/integracaoprologtestes}Avaliacao" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAvaliacao", propOrder = {
    "avaliacao"
})
public class ArrayOfAvaliacao {

    @XmlElement(name = "Avaliacao", nillable = true)
    protected List<Avaliacao> avaliacao;

    /**
     * Gets the value of the avaliacao property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the avaliacao property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvaliacao().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Avaliacao }
     * 
     * 
     */
    public List<Avaliacao> getAvaliacao() {
        if (avaliacao == null) {
            avaliacao = new ArrayList<Avaliacao>();
        }
        return this.avaliacao;
    }

}
