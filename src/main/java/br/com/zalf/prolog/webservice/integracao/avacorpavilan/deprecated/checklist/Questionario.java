
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Questionario complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Questionario">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoQuestionario" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="vinculoCliente" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nomeCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Questionario", propOrder = {
    "codigoQuestionario",
    "vinculoCliente",
    "cliente",
    "descricao",
    "nomeCliente"
})
public class Questionario {

    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer codigoQuestionario;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer vinculoCliente;
    protected String cliente;
    protected String descricao;
    protected String nomeCliente;

    /**
     * Obtém o valor da propriedade codigoQuestionario.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodigoQuestionario() {
        return codigoQuestionario;
    }

    /**
     * Define o valor da propriedade codigoQuestionario.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodigoQuestionario(Integer value) {
        this.codigoQuestionario = value;
    }

    /**
     * Obtém o valor da propriedade vinculoCliente.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVinculoCliente() {
        return vinculoCliente;
    }

    /**
     * Define o valor da propriedade vinculoCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVinculoCliente(Integer value) {
        this.vinculoCliente = value;
    }

    /**
     * Obtém o valor da propriedade cliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Define o valor da propriedade cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCliente(String value) {
        this.cliente = value;
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
     * Obtém o valor da propriedade nomeCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeCliente() {
        return nomeCliente;
    }

    /**
     * Define o valor da propriedade nomeCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeCliente(String value) {
        this.nomeCliente = value;
    }

}
