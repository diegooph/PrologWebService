
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de AlternativasNokVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="AlternativasNokVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodAlternativaNok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DescricaoAlternativaNok" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrioridadeAlternativaNok" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AlternativasNokVO", propOrder = {
    "codAlternativaNok",
    "descricaoAlternativaNok",
    "prioridadeAlternativaNok"
})
public class AlternativasNokVO {

    @XmlElement(name = "CodAlternativaNok")
    protected int codAlternativaNok;
    @XmlElement(name = "DescricaoAlternativaNok")
    protected String descricaoAlternativaNok;
    @XmlElement(name = "PrioridadeAlternativaNok")
    protected String prioridadeAlternativaNok;

    /**
     * Obtém o valor da propriedade codAlternativaNok.
     * 
     */
    public int getCodAlternativaNok() {
        return codAlternativaNok;
    }

    /**
     * Define o valor da propriedade codAlternativaNok.
     * 
     */
    public void setCodAlternativaNok(int value) {
        this.codAlternativaNok = value;
    }

    /**
     * Obtém o valor da propriedade descricaoAlternativaNok.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescricaoAlternativaNok() {
        return descricaoAlternativaNok;
    }

    /**
     * Define o valor da propriedade descricaoAlternativaNok.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescricaoAlternativaNok(String value) {
        this.descricaoAlternativaNok = value;
    }

    /**
     * Obtém o valor da propriedade prioridadeAlternativaNok.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrioridadeAlternativaNok() {
        return prioridadeAlternativaNok;
    }

    /**
     * Define o valor da propriedade prioridadeAlternativaNok.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrioridadeAlternativaNok(String value) {
        this.prioridadeAlternativaNok = value;
    }

}
