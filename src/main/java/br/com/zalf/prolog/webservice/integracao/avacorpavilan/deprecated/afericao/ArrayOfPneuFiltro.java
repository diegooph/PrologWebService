
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfPneuFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPneuFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PneuFiltro" type="{http://www.avacorp.com.br/integracaoprologtestes}PneuFiltro" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPneuFiltro", propOrder = {
    "pneuFiltro"
})
public class ArrayOfPneuFiltro {

    @XmlElement(name = "PneuFiltro", nillable = true)
    protected List<PneuFiltro> pneuFiltro;

    /**
     * Gets the value of the pneuFiltro property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pneuFiltro property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPneuFiltro().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PneuFiltro }
     * 
     * 
     */
    public List<PneuFiltro> getPneuFiltro() {
        if (pneuFiltro == null) {
            pneuFiltro = new ArrayList<PneuFiltro>();
        }
        return this.pneuFiltro;
    }

}
