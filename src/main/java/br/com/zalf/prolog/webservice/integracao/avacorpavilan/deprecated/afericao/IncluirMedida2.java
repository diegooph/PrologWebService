
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanTipoMarcador;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IncluirMedida complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IncluirMedida">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="veiculo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="marcador" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tipoMarcador" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="carreta1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="carreta2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="carreta3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="medidas" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfMedidaPneu" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IncluirMedida", propOrder = {
    "veiculo",
    "marcador",
    "tipoMarcador",
    "carreta1",
    "carreta2",
    "carreta3",
    "dataMedida",
    "medidas",
    "cpfColaborador"
})
public class IncluirMedida2 {

    protected String veiculo;
    protected int marcador;
    protected int tipoMarcador;
    protected String carreta1;
    protected String carreta2;
    protected String carreta3;
    protected String dataMedida;
    protected ArrayOfMedidaPneu medidas;
    protected String cpfColaborador;

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
     * Gets the value of the tipoMarcador property.
     * 
     */
    public AvaCorpAvilanTipoMarcador getTipoMarcador() {
        return AvaCorpAvilanTipoMarcador.fromInt(tipoMarcador);
    }

    /**
     * Sets the value of the tipoMarcador property.
     * 
     */
    public void setTipoMarcador(AvaCorpAvilanTipoMarcador tipoMarcador) {
        this.tipoMarcador = tipoMarcador.asInt();
    }

    /**
     * Gets the value of the carreta1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarreta1() {
        return carreta1;
    }

    /**
     * Sets the value of the carreta1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarreta1(String value) {
        this.carreta1 = value;
    }

    /**
     * Gets the value of the carreta2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarreta2() {
        return carreta2;
    }

    /**
     * Sets the value of the carreta2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarreta2(String value) {
        this.carreta2 = value;
    }

    /**
     * Gets the value of the carreta3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarreta3() {
        return carreta3;
    }

    /**
     * Sets the value of the carreta3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarreta3(String value) {
        this.carreta3 = value;
    }

    /**
     * Gets the value of the dataMedida property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataMedida() {
        return dataMedida;
    }

    /**
     * Sets the value of the dataMedida property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataMedida(String value) {
        this.dataMedida = value;
    }

    /**
     * Gets the value of the medidas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMedidaPneu }
     *     
     */
    public ArrayOfMedidaPneu getMedidas() {
        return medidas;
    }

    /**
     * Sets the value of the medidas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMedidaPneu }
     *     
     */
    public void setMedidas(ArrayOfMedidaPneu value) {
        this.medidas = value;
    }

    /**
     * Gets the value of the cpfColaborador property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCpfColaborador() {
        return cpfColaborador;
    }

    /**
     * Sets the value of the cpfColaborador property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCpfColaborador(String value) {
        this.cpfColaborador = value;
    }

}
