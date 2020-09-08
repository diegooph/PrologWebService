
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="buscarChecklistFiltroResult" type="{http://www.avacorp.com.br/integracaoprologtestes}ChecklistsFiltro" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "buscarChecklistFiltroResult"
})
@XmlRootElement(name = "buscarChecklistFiltroResponse")
public class BuscarChecklistFiltroResponse {

    protected ChecklistsFiltro buscarChecklistFiltroResult;

    /**
     * Gets the value of the buscarChecklistFiltroResult property.
     * 
     * @return
     *     possible object is
     *     {@link ChecklistsFiltro }
     *     
     */
    public ChecklistsFiltro getBuscarChecklistFiltroResult() {
        return buscarChecklistFiltroResult;
    }

    /**
     * Sets the value of the buscarChecklistFiltroResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChecklistsFiltro }
     *     
     */
    public void setBuscarChecklistFiltroResult(ChecklistsFiltro value) {
        this.buscarChecklistFiltroResult = value;
    }

}
