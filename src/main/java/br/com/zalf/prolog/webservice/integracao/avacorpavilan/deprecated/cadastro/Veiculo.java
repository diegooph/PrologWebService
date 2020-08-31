
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Veiculo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Veiculo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="marcador" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quantidadePneu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dtUltimaAfericao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Veiculo", propOrder = {
    "placa",
    "marcador",
    "modelo",
    "tipoVeiculo",
    "quantidadePneu",
    "dtUltimaAfericao"
})
public class Veiculo {

    protected String placa;
    protected int marcador;
    protected String modelo;
    protected TipoVeiculoAvilan tipoVeiculo;
    protected int quantidadePneu;
    protected String dtUltimaAfericao;

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
     * Gets the value of the marcador property.
     * 
     */
    public int getMarcador() {
        return marcador;
    }

    /**
     * Sets the value of the marcador property.
     * 
     */
    public void setMarcador(int value) {
        this.marcador = value;
    }

    /**
     * Gets the value of the modelo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Sets the value of the modelo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelo(String value) {
        this.modelo = value;
    }

    public TipoVeiculoAvilan getTipo() {
        return tipoVeiculo;
    }

    public void setTipo(TipoVeiculoAvilan tipo) {
        this.tipoVeiculo = tipo;
    }

    /**
     * Gets the value of the quantidadePneu property.
     * 
     */
    public int getQuantidadePneu() {
        return quantidadePneu;
    }

    /**
     * Sets the value of the quantidadePneu property.
     * 
     */
    public void setQuantidadePneu(int value) {
        this.quantidadePneu = value;
    }

    /**
     * Gets the value of the dtUltimaAfericao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDtUltimaAfericao() {
        return dtUltimaAfericao;
    }

    /**
     * Sets the value of the dtUltimaAfericao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDtUltimaAfericao(String value) {
        this.dtUltimaAfericao = value;
    }

}
