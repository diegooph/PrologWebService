
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfItemCritico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfItemCritico">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ItemCritico" type="{http://www.avacorp.com.br/integracaoprologtestes}ItemCritico" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfItemCritico", propOrder = {
    "itemCritico"
})
public class ArrayOfItemCritico {

    @XmlElement(name = "ItemCritico", nillable = true)
    protected List<ItemCritico> itemCritico;

    /**
     * Gets the value of the itemCritico property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemCritico property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemCritico().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemCritico }
     * 
     * 
     */
    public List<ItemCritico> getItemCritico() {
        if (itemCritico == null) {
            itemCritico = new ArrayList<ItemCritico>();
        }
        return this.itemCritico;
    }

}
