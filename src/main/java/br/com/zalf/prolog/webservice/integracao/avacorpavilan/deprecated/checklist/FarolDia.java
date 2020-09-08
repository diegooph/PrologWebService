
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FarolDia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarolDia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="veiculosChecklist" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfVeiculoChecklist" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarolDia", propOrder = {
    "data",
    "veiculosChecklist"
})
public class FarolDia {

    protected String data;
    protected ArrayOfVeiculoChecklist veiculosChecklist;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(String value) {
        this.data = value;
    }

    /**
     * Gets the value of the veiculosChecklist property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVeiculoChecklist }
     *     
     */
    public ArrayOfVeiculoChecklist getVeiculosChecklist() {
        return veiculosChecklist;
    }

    /**
     * Sets the value of the veiculosChecklist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVeiculoChecklist }
     *     
     */
    public void setVeiculosChecklist(ArrayOfVeiculoChecklist value) {
        this.veiculosChecklist = value;
    }

}
