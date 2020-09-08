
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Resposta complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
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
     * Obtém o valor da propriedade codigoResposta.
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
     * Define o valor da propriedade codigoResposta.
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
     * Obtém o valor da propriedade descricao.
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
     * Define o valor da propriedade descricao.
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
     * Obtém o valor da propriedade obrigatorioObservacao.
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
     * Define o valor da propriedade obrigatorioObservacao.
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
     * Obtém o valor da propriedade padrao.
     * 
     */
    public boolean isPadrao() {
        return padrao;
    }

    /**
     * Define o valor da propriedade padrao.
     * 
     */
    public void setPadrao(boolean value) {
        this.padrao = value;
    }

}
