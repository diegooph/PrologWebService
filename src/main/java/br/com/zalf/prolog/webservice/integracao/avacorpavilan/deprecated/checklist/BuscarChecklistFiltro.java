
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
 *         &lt;element name="filial" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="unidade" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataInicial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataFinal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="veiculo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoVeiculo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "filial",
    "unidade",
    "dataInicial",
    "dataFinal",
    "veiculo",
    "tipoVeiculo"
})
@XmlRootElement(name = "buscarChecklistFiltro")
public class BuscarChecklistFiltro {

    protected int filial;
    protected int unidade;
    protected String dataInicial;
    protected String dataFinal;
    protected String veiculo;
    protected String tipoVeiculo;

    /**
     * Gets the value of the filial property.
     * 
     */
    public int getFilial() {
        return filial;
    }

    /**
     * Sets the value of the filial property.
     * 
     */
    public void setFilial(int value) {
        this.filial = value;
    }

    /**
     * Gets the value of the unidade property.
     * 
     */
    public int getUnidade() {
        return unidade;
    }

    /**
     * Sets the value of the unidade property.
     * 
     */
    public void setUnidade(int value) {
        this.unidade = value;
    }

    /**
     * Gets the value of the dataInicial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataInicial() {
        return dataInicial;
    }

    /**
     * Sets the value of the dataInicial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataInicial(String value) {
        this.dataInicial = value;
    }

    /**
     * Gets the value of the dataFinal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataFinal() {
        return dataFinal;
    }

    /**
     * Sets the value of the dataFinal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataFinal(String value) {
        this.dataFinal = value;
    }

    /**
     * Gets the value of the veiculo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVeiculo() {
        return veiculo;
    }

    /**
     * Sets the value of the veiculo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVeiculo(String value) {
        this.veiculo = value;
    }

    /**
     * Gets the value of the tipoVeiculo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    /**
     * Sets the value of the tipoVeiculo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoVeiculo(String value) {
        this.tipoVeiculo = value;
    }

}
