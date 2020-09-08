
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
 *         &lt;element name="buscarPneusVeiculoResult" type="{http://www.avacorp.com.br/integracaoprolog}PneusVeiculo" minOccurs="0"/>
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
    "buscarPneusVeiculoResult"
})
@XmlRootElement(name = "buscarPneusVeiculoResponse")
public class BuscarPneusVeiculoResponse {

    protected PneusVeiculo buscarPneusVeiculoResult;

    /**
     * Gets the value of the buscarPneusVeiculoResult property.
     * 
     * @return
     *     possible object is
     *     {@link PneusVeiculo }
     *     
     */
    public PneusVeiculo getBuscarPneusVeiculoResult() {
        return buscarPneusVeiculoResult;
    }

    /**
     * Sets the value of the buscarPneusVeiculoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link PneusVeiculo }
     *     
     */
    public void setBuscarPneusVeiculoResult(PneusVeiculo value) {
        this.buscarPneusVeiculoResult = value;
    }

}
