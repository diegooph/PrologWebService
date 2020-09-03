
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfTipoVeiculo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTipoVeiculo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TipoVeiculo" type="{http://www.avacorp.com.br/integracaoprologtestes}TipoVeiculo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTipoVeiculo", propOrder = {
    "tipoVeiculo"
})
public class ArrayOfTipoVeiculo {

    @XmlElement(name = "TipoVeiculo", nillable = true)
    protected List<TipoVeiculoAvilan> tipoVeiculo;

    /**
     * Gets the value of the tipoVeiculo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tipoVeiculo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTipoVeiculo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TipoVeiculoAvilan }
     * 
     * 
     */
    public List<TipoVeiculoAvilan> getTipoVeiculo() {
        if (tipoVeiculo == null) {
            tipoVeiculo = new ArrayList<TipoVeiculoAvilan>();
        }
        return this.tipoVeiculo;
    }

}
