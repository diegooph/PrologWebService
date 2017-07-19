
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Resposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Resposta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoResposta" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obrigatorioObservacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="padrao" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resposta", propOrder = {
    "codigoResposta",
    "descricao",
    "obrigatorioObservacao",
    "padrao"
})
public class Resposta {

    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer codigoResposta;
    protected String descricao;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer obrigatorioObservacao;
    protected boolean padrao;

    /**
     * Gets the value of the codigoResposta property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodigoResposta() {
        return codigoResposta;
    }

    /**
     * Sets the value of the codigoResposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodigoResposta(Integer value) {
        this.codigoResposta = value;
    }

    /**
     * Gets the value of the descricao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Sets the value of the descricao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescricao(String value) {
        this.descricao = value;
    }

    /**
     * Gets the value of the obrigatorioObservacao property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getObrigatorioObservacao() {
        return obrigatorioObservacao;
    }

    /**
     * Sets the value of the obrigatorioObservacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setObrigatorioObservacao(Integer value) {
        this.obrigatorioObservacao = value;
    }

    /**
     * Gets the value of the padrao property.
     * 
     */
    public boolean isPadrao() {
        return padrao;
    }

    /**
     * Sets the value of the padrao property.
     * 
     */
    public void setPadrao(boolean value) {
        this.padrao = value;
    }

}
