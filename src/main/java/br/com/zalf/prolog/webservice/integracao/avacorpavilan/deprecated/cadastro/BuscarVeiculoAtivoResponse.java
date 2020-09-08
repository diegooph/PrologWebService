
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
 *         &lt;element name="buscarVeiculoAtivoResult" type="{http://www.avacorp.com.br/integracaoprolog}VeiculosAtivos" minOccurs="0"/>
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
    "buscarVeiculoAtivoResult"
})
@XmlRootElement(name = "buscarVeiculoAtivoResponse")
public class BuscarVeiculoAtivoResponse {

    protected VeiculosAtivos buscarVeiculoAtivoResult;

    /**
     * Gets the value of the buscarVeiculoAtivoResult property.
     * 
     * @return
     *     possible object is
     *     {@link VeiculosAtivos }
     *     
     */
    public VeiculosAtivos getBuscarVeiculoAtivoResult() {
        return buscarVeiculoAtivoResult;
    }

    /**
     * Sets the value of the buscarVeiculoAtivoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link VeiculosAtivos }
     *     
     */
    public void setBuscarVeiculoAtivoResult(VeiculosAtivos value) {
        this.buscarVeiculoAtivoResult = value;
    }

}
