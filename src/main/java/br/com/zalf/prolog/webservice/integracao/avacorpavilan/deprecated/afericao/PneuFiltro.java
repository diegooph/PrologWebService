
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PneuFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PneuFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroFogo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="posicao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pressao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="trianguloPrimeiroSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="trianguloSegundoSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="trianguloTerceiroSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="trianguloQuartoSulco" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
@XmlType(name = "PneuFiltro", propOrder = {
    "numeroFogo",
    "posicao",
    "pressao",
    "trianguloPrimeiroSulco",
    "trianguloSegundoSulco",
    "trianguloTerceiroSulco",
    "trianguloQuartoSulco",
    "pressaoRecomendada"
})
public class PneuFiltro {

    protected String numeroFogo;
    protected String posicao;
    protected int pressao;
    protected double trianguloPrimeiroSulco;
    protected double trianguloSegundoSulco;
    protected double trianguloTerceiroSulco;
    protected double trianguloQuartoSulco;
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
     * Gets the value of the pressao property.
     * 
     */
    public int getPressao() {
        return pressao;
    }

    /**
     * Sets the value of the pressao property.
     * 
     */
    public void setPressao(int value) {
        this.pressao = value;
    }

    /**
     * Gets the value of the trianguloPrimeiroSulco property.
     * 
     */
    public double getTrianguloPrimeiroSulco() {
        return trianguloPrimeiroSulco;
    }

    /**
     * Sets the value of the trianguloPrimeiroSulco property.
     * 
     */
    public void setTrianguloPrimeiroSulco(double value) {
        this.trianguloPrimeiroSulco = value;
    }

    /**
     * Gets the value of the trianguloSegundoSulco property.
     * 
     */
    public double getTrianguloSegundoSulco() {
        return trianguloSegundoSulco;
    }

    /**
     * Sets the value of the trianguloSegundoSulco property.
     * 
     */
    public void setTrianguloSegundoSulco(double value) {
        this.trianguloSegundoSulco = value;
    }

    /**
     * Gets the value of the trianguloTerceiroSulco property.
     * 
     */
    public double getTrianguloTerceiroSulco() {
        return trianguloTerceiroSulco;
    }

    /**
     * Sets the value of the trianguloTerceiroSulco property.
     * 
     */
    public void setTrianguloTerceiroSulco(double value) {
        this.trianguloTerceiroSulco = value;
    }

    /**
     * Gets the value of the trianguloQuartoSulco property.
     * 
     */
    public double getTrianguloQuartoSulco() {
        return trianguloQuartoSulco;
    }

    /**
     * Sets the value of the trianguloQuartoSulco property.
     * 
     */
    public void setTrianguloQuartoSulco(double value) {
        this.trianguloQuartoSulco = value;
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