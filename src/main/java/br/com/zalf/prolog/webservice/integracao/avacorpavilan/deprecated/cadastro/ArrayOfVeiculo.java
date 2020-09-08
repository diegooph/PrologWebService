
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfVeiculo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfVeiculo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Veiculo" type="{http://www.avacorp.com.br/integracaoprolog}Veiculo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVeiculo", propOrder = {
    "veiculo"
})
public class ArrayOfVeiculo {

    @XmlElement(name = "Veiculo", nillable = true)
    protected List<Veiculo> veiculo;

    /**
     * Gets the value of the veiculo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the veiculo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVeiculo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Veiculo }
     * 
     * 
     */
    public List<Veiculo> getVeiculo() {
        if (veiculo == null) {
            veiculo = new ArrayList<Veiculo>();
        }
        return this.veiculo;
    }

}
