
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Pneu complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
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
    "vidaPneu"
})
public class Pneu {

    protected String numeroFogo;
    protected String posicao;
    protected double sulco1;
    protected double sulco2;
    protected double sulco3;
    protected double sulco4;
    protected int vidaPneu;

    /**
     * Obtém o valor da propriedade numeroFogo.
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
     * Define o valor da propriedade numeroFogo.
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
     * Obtém o valor da propriedade posicao.
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
     * Define o valor da propriedade posicao.
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
     * Obtém o valor da propriedade sulco1.
     * 
     */
    public double getSulco1() {
        return sulco1;
    }

    /**
     * Define o valor da propriedade sulco1.
     * 
     */
    public void setSulco1(double value) {
        this.sulco1 = value;
    }

    /**
     * Obtém o valor da propriedade sulco2.
     * 
     */
    public double getSulco2() {
        return sulco2;
    }

    /**
     * Define o valor da propriedade sulco2.
     * 
     */
    public void setSulco2(double value) {
        this.sulco2 = value;
    }

    /**
     * Obtém o valor da propriedade sulco3.
     * 
     */
    public double getSulco3() {
        return sulco3;
    }

    /**
     * Define o valor da propriedade sulco3.
     * 
     */
    public void setSulco3(double value) {
        this.sulco3 = value;
    }

    /**
     * Obtém o valor da propriedade sulco4.
     * 
     */
    public double getSulco4() {
        return sulco4;
    }

    /**
     * Define o valor da propriedade sulco4.
     * 
     */
    public void setSulco4(double value) {
        this.sulco4 = value;
    }

    /**
     * Obtém o valor da propriedade vidaPneu.
     * 
     */
    public int getVidaPneu() {
        return vidaPneu;
    }

    /**
     * Define o valor da propriedade vidaPneu.
     * 
     */
    public void setVidaPneu(int value) {
        this.vidaPneu = value;
    }

}
