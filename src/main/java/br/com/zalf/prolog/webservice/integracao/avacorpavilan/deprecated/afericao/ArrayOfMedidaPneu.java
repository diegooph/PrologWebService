
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfMedidaPneu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfMedidaPneu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MedidaPneu" type="{http://www.avacorp.com.br/integracaoprolog}MedidaPneu" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfMedidaPneu", propOrder = {
    "medidaPneu"
})
public class ArrayOfMedidaPneu {

    @XmlElement(name = "MedidaPneu", nillable = true)
    protected List<MedidaPneu> medidaPneu;

    /**
     * Gets the value of the medidaPneu property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the medidaPneu property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMedidaPneu().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MedidaPneu }
     * 
     * 
     */
    public List<MedidaPneu> getMedidaPneu() {
        if (medidaPneu == null) {
            medidaPneu = new ArrayList<MedidaPneu>();
        }
        return this.medidaPneu;
    }

}
