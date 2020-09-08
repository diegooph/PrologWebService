
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfFarolDia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfFarolDia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FarolDia" type="{http://www.avacorp.com.br/integracaoprologtestes}FarolDia" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfFarolDia", propOrder = {
    "farolDia"
})
public class ArrayOfFarolDia {

    @XmlElement(name = "FarolDia", nillable = true)
    protected List<FarolDia> farolDia;

    /**
     * Gets the value of the farolDia property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the farolDia property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFarolDia().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FarolDia }
     * 
     * 
     */
    public List<FarolDia> getFarolDia() {
        if (farolDia == null) {
            farolDia = new ArrayList<FarolDia>();
        }
        return this.farolDia;
    }

}
