
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
 *         &lt;element name="buscarTiposVeiculoResult" type="{http://www.avacorp.com.br/integracaoprologtestes}TiposVeiculo" minOccurs="0"/>
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
    "buscarTiposVeiculoResult"
})
@XmlRootElement(name = "buscarTiposVeiculoResponse")
public class BuscarTiposVeiculoResponse {

    protected TiposVeiculo buscarTiposVeiculoResult;

    /**
     * Gets the value of the buscarTiposVeiculoResult property.
     * 
     * @return
     *     possible object is
     *     {@link TiposVeiculo }
     *     
     */
    public TiposVeiculo getBuscarTiposVeiculoResult() {
        return buscarTiposVeiculoResult;
    }

    /**
     * Sets the value of the buscarTiposVeiculoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link TiposVeiculo }
     *     
     */
    public void setBuscarTiposVeiculoResult(TiposVeiculo value) {
        this.buscarTiposVeiculoResult = value;
    }

}
