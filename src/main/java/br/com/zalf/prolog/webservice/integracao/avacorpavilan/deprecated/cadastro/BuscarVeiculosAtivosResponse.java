
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

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
 *         &lt;element name="buscarVeiculosAtivosResult" type="{http://www.avacorp.com.br/integracaoprolog}VeiculosAtivos" minOccurs="0"/>
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
    "buscarVeiculosAtivosResult"
})
@XmlRootElement(name = "buscarVeiculosAtivosResponse")
public class BuscarVeiculosAtivosResponse {

    protected VeiculosAtivos buscarVeiculosAtivosResult;

    /**
     * Gets the value of the buscarVeiculosAtivosResult property.
     * 
     * @return
     *     possible object is
     *     {@link VeiculosAtivos }
     *     
     */
    public VeiculosAtivos getBuscarVeiculosAtivosResult() {
        return buscarVeiculosAtivosResult;
    }

    /**
     * Sets the value of the buscarVeiculosAtivosResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link VeiculosAtivos }
     *     
     */
    public void setBuscarVeiculosAtivosResult(VeiculosAtivos value) {
        this.buscarVeiculosAtivosResult = value;
    }

}
