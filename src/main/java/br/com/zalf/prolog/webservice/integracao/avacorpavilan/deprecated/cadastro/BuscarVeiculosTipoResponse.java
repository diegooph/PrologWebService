
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
 *         &lt;element name="buscarVeiculosTipoResult" type="{http://www.avacorp.com.br/integracaoprologtestes}VeiculoTipo" minOccurs="0"/>
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
    "buscarVeiculosTipoResult"
})
@XmlRootElement(name = "buscarVeiculosTipoResponse")
public class BuscarVeiculosTipoResponse {

    protected VeiculoTipo buscarVeiculosTipoResult;

    /**
     * Gets the value of the buscarVeiculosTipoResult property.
     * 
     * @return
     *     possible object is
     *     {@link VeiculoTipo }
     *     
     */
    public VeiculoTipo getBuscarVeiculosTipoResult() {
        return buscarVeiculosTipoResult;
    }

    /**
     * Sets the value of the buscarVeiculosTipoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link VeiculoTipo }
     *     
     */
    public void setBuscarVeiculosTipoResult(VeiculoTipo value) {
        this.buscarVeiculosTipoResult = value;
    }

}
