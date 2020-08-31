
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VeiculoChecklist complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VeiculoChecklist">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descItemCritico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descAvaliacoes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="itensCriticos" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfItemCritico" minOccurs="0"/>
 *         &lt;element name="avaliacoes" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfAvaliacao" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VeiculoChecklist", propOrder = {
    "data",
    "placa",
    "descItemCritico",
    "descAvaliacoes",
    "itensCriticos",
    "avaliacoes"
})
public class VeiculoChecklist {

    protected String data;
    protected String placa;
    protected String descItemCritico;
    protected String descAvaliacoes;
    protected ArrayOfItemCritico itensCriticos;
    protected ArrayOfAvaliacao avaliacoes;

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
     * Gets the value of the placa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Sets the value of the placa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaca(String value) {
        this.placa = value;
    }

    /**
     * Gets the value of the descItemCritico property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescItemCritico() {
        return descItemCritico;
    }

    /**
     * Sets the value of the descItemCritico property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescItemCritico(String value) {
        this.descItemCritico = value;
    }

    /**
     * Gets the value of the descAvaliacoes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescAvaliacoes() {
        return descAvaliacoes;
    }

    /**
     * Sets the value of the descAvaliacoes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescAvaliacoes(String value) {
        this.descAvaliacoes = value;
    }

    /**
     * Gets the value of the itensCriticos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfItemCritico }
     *     
     */
    public ArrayOfItemCritico getItensCriticos() {
        return itensCriticos;
    }

    /**
     * Sets the value of the itensCriticos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfItemCritico }
     *     
     */
    public void setItensCriticos(ArrayOfItemCritico value) {
        this.itensCriticos = value;
    }

    /**
     * Gets the value of the avaliacoes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAvaliacao }
     *     
     */
    public ArrayOfAvaliacao getAvaliacoes() {
        return avaliacoes;
    }

    /**
     * Sets the value of the avaliacoes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAvaliacao }
     *     
     */
    public void setAvaliacoes(ArrayOfAvaliacao value) {
        this.avaliacoes = value;
    }

}
