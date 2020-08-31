
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Pneu complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Pneu">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroFogo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="posicao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sulco1" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="sulco2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="sulco3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="sulco4" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="vidaPneu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pressaoRecomendada" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Pneu", propOrder = {
    "numeroFogo",
    "posicao",
    "sulco1",
    "sulco2",
    "sulco3",
    "sulco4",
    "vidaPneu",
    "pressaoRecomendada"
})
public class Pneu {

    protected String numeroFogo;
    protected String posicao;
    protected double sulco1;
    protected double sulco2;
    protected double sulco3;
    protected double sulco4;
    protected int vidaPneu;
    protected int pressaoRecomendada;

    /**
     * Gets the value of the numeroFogo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroFogo() {
        return numeroFogo;
    }

    /**
     * Sets the value of the numeroFogo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroFogo(String value) {
        this.numeroFogo = value;
    }

    /**
     * Gets the value of the posicao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosicao() {
        return posicao;
    }

    /**
     * Sets the value of the posicao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosicao(String value) {
        this.posicao = value;
    }

    /**
     * Gets the value of the sulco1 property.
     * 
     */
    public double getSulco1() {
        return sulco1;
    }

    /**
     * Sets the value of the sulco1 property.
     * 
     */
    public void setSulco1(double value) {
        this.sulco1 = value;
    }

    /**
     * Gets the value of the sulco2 property.
     * 
     */
    public double getSulco2() {
        return sulco2;
    }

    /**
     * Sets the value of the sulco2 property.
     * 
     */
    public void setSulco2(double value) {
        this.sulco2 = value;
    }

    /**
     * Gets the value of the sulco3 property.
     * 
     */
    public double getSulco3() {
        return sulco3;
    }

    /**
     * Sets the value of the sulco3 property.
     * 
     */
    public void setSulco3(double value) {
        this.sulco3 = value;
    }

    /**
     * Gets the value of the sulco4 property.
     * 
     */
    public double getSulco4() {
        return sulco4;
    }

    /**
     * Sets the value of the sulco4 property.
     * 
     */
    public void setSulco4(double value) {
        this.sulco4 = value;
    }

    /**
     * Gets the value of the vidaPneu property.
     * 
     */
    public int getVidaPneu() {
        return vidaPneu;
    }

    /**
     * Sets the value of the vidaPneu property.
     * 
     */
    public void setVidaPneu(int value) {
        this.vidaPneu = value;
    }

    /**
     * Gets the value of the pressaoRecomendada property.
     *
     */
    public int getPressaoRecomendada() {
        return pressaoRecomendada;
    }

    /**
     * Sets the value of the pressaoRecomendada property.
     *
     */
    public void setPressaoRecomendada(final int pressaoRecomendada) {
        this.pressaoRecomendada = pressaoRecomendada;
    }
}