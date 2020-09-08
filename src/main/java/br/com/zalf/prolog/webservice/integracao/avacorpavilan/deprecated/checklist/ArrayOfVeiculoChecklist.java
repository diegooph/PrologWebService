
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfVeiculoChecklist complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfVeiculoChecklist">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VeiculoChecklist" type="{http://www.avacorp.com.br/integracaoprologtestes}VeiculoChecklist" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVeiculoChecklist", propOrder = {
    "veiculoChecklist"
})
public class ArrayOfVeiculoChecklist {

    @XmlElement(name = "VeiculoChecklist", nillable = true)
    protected List<VeiculoChecklist> veiculoChecklist;

    /**
     * Gets the value of the veiculoChecklist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the veiculoChecklist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVeiculoChecklist().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VeiculoChecklist }
     * 
     * 
     */
    public List<VeiculoChecklist> getVeiculoChecklist() {
        if (veiculoChecklist == null) {
            veiculoChecklist = new ArrayList<VeiculoChecklist>();
        }
        return this.veiculoChecklist;
    }

}
