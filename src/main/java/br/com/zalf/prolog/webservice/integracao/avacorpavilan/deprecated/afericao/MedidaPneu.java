
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MedidaPneu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedidaPneu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroFogoPneu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="triangulo1PrimeiroSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="triangulo1SegundoSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="triangulo1TerceiroSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="triangulo1QuartoSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="calibragem" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedidaPneu", propOrder = {
    "numeroFogoPneu",
    "triangulo1PrimeiroSulco",
    "triangulo1SegundoSulco",
    "triangulo1TerceiroSulco",
    "triangulo1QuartoSulco",
    "calibragem"
})
public class MedidaPneu {

    protected String numeroFogoPneu;
    protected double triangulo1PrimeiroSulco;
    protected double triangulo1SegundoSulco;
    protected double triangulo1TerceiroSulco;
    protected double triangulo1QuartoSulco;
    protected int calibragem;

    /**
     * Gets the value of the numeroFogoPneu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroFogoPneu() {
        return numeroFogoPneu;
    }

    /**
     * Sets the value of the numeroFogoPneu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroFogoPneu(String value) {
        this.numeroFogoPneu = value;
    }

    /**
     * Gets the value of the triangulo1PrimeiroSulco property.
     * 
     */
    public double getTriangulo1PrimeiroSulco() {
        return triangulo1PrimeiroSulco;
    }

    /**
     * Sets the value of the triangulo1PrimeiroSulco property.
     * 
     */
    public void setTriangulo1PrimeiroSulco(double value) {
        this.triangulo1PrimeiroSulco = value;
    }

    /**
     * Gets the value of the triangulo1SegundoSulco property.
     * 
     */
    public double getTriangulo1SegundoSulco() {
        return triangulo1SegundoSulco;
    }

    /**
     * Sets the value of the triangulo1SegundoSulco property.
     * 
     */
    public void setTriangulo1SegundoSulco(double value) {
        this.triangulo1SegundoSulco = value;
    }

    /**
     * Gets the value of the triangulo1TerceiroSulco property.
     * 
     */
    public double getTriangulo1TerceiroSulco() {
        return triangulo1TerceiroSulco;
    }

    /**
     * Sets the value of the triangulo1TerceiroSulco property.
     * 
     */
    public void setTriangulo1TerceiroSulco(double value) {
        this.triangulo1TerceiroSulco = value;
    }

    /**
     * Gets the value of the triangulo1QuartoSulco property.
     * 
     */
    public double getTriangulo1QuartoSulco() {
        return triangulo1QuartoSulco;
    }

    /**
     * Sets the value of the triangulo1QuartoSulco property.
     * 
     */
    public void setTriangulo1QuartoSulco(double value) {
        this.triangulo1QuartoSulco = value;
    }

    /**
     * Gets the value of the calibragem property.
     * 
     */
    public int getCalibragem() {
        return calibragem;
    }

    /**
     * Sets the value of the calibragem property.
     * 
     */
    public void setCalibragem(int value) {
        this.calibragem = value;
    }

}
