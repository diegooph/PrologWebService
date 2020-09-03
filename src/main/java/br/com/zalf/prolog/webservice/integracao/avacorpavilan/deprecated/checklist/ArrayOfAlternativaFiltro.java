
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfAlternativaFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAlternativaFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AlternativaFiltro" type="{http://www.avacorp.com.br/integracaoprologtestes}AlternativaFiltro" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAlternativaFiltro", propOrder = {
    "alternativaFiltro"
})
public class ArrayOfAlternativaFiltro {

    @XmlElement(name = "AlternativaFiltro", nillable = true)
    protected List<AlternativaFiltro> alternativaFiltro;

    /**
     * Gets the value of the alternativaFiltro property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternativaFiltro property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternativaFiltro().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AlternativaFiltro }
     * 
     * 
     */
    public List<AlternativaFiltro> getAlternativaFiltro() {
        if (alternativaFiltro == null) {
            alternativaFiltro = new ArrayList<AlternativaFiltro>();
        }
        return this.alternativaFiltro;
    }

}
