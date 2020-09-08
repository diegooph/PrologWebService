
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfAfericaoFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAfericaoFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AfericaoFiltro" type="{http://www.avacorp.com.br/integracaoprologtestes}AfericaoFiltro" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAfericaoFiltro", propOrder = {
    "afericaoFiltro"
})
public class ArrayOfAfericaoFiltro {

    @XmlElement(name = "AfericaoFiltro", nillable = true)
    protected List<AfericaoFiltro> afericaoFiltro;

    /**
     * Gets the value of the afericaoFiltro property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the afericaoFiltro property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAfericaoFiltro().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AfericaoFiltro }
     * 
     * 
     */
    public List<AfericaoFiltro> getAfericaoFiltro() {
        if (afericaoFiltro == null) {
            afericaoFiltro = new ArrayList<AfericaoFiltro>();
        }
        return this.afericaoFiltro;
    }

}
