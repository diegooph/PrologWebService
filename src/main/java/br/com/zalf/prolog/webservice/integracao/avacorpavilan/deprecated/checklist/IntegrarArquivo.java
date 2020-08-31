
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipodocumento" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="diferenciadornumero" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serie" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numerosequencia" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numerosequenciaitem" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cnpjcpfcodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dtemissao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nomearquivo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extensao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="conteudo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tipodocumento",
    "diferenciadornumero",
    "serie",
    "numerosequencia",
    "numerosequenciaitem",
    "cnpjcpfcodigo",
    "dtemissao",
    "nomearquivo",
    "extensao",
    "conteudo"
})
@XmlRootElement(name = "integrarArquivo")
public class IntegrarArquivo {

    protected int tipodocumento;
    protected int diferenciadornumero;
    protected int serie;
    protected int numerosequencia;
    protected int numerosequenciaitem;
    protected String cnpjcpfcodigo;
    protected String dtemissao;
    protected String nomearquivo;
    protected String extensao;
    protected String conteudo;

    /**
     * Obtém o valor da propriedade tipodocumento.
     * 
     */
    public int getTipodocumento() {
        return tipodocumento;
    }

    /**
     * Define o valor da propriedade tipodocumento.
     * 
     */
    public void setTipodocumento(int value) {
        this.tipodocumento = value;
    }

    /**
     * Obtém o valor da propriedade diferenciadornumero.
     * 
     */
    public int getDiferenciadornumero() {
        return diferenciadornumero;
    }

    /**
     * Define o valor da propriedade diferenciadornumero.
     * 
     */
    public void setDiferenciadornumero(int value) {
        this.diferenciadornumero = value;
    }

    /**
     * Obtém o valor da propriedade serie.
     * 
     */
    public int getSerie() {
        return serie;
    }

    /**
     * Define o valor da propriedade serie.
     * 
     */
    public void setSerie(int value) {
        this.serie = value;
    }

    /**
     * Obtém o valor da propriedade numerosequencia.
     * 
     */
    public int getNumerosequencia() {
        return numerosequencia;
    }

    /**
     * Define o valor da propriedade numerosequencia.
     * 
     */
    public void setNumerosequencia(int value) {
        this.numerosequencia = value;
    }

    /**
     * Obtém o valor da propriedade numerosequenciaitem.
     * 
     */
    public int getNumerosequenciaitem() {
        return numerosequenciaitem;
    }

    /**
     * Define o valor da propriedade numerosequenciaitem.
     * 
     */
    public void setNumerosequenciaitem(int value) {
        this.numerosequenciaitem = value;
    }

    /**
     * Obtém o valor da propriedade cnpjcpfcodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCnpjcpfcodigo() {
        return cnpjcpfcodigo;
    }

    /**
     * Define o valor da propriedade cnpjcpfcodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCnpjcpfcodigo(String value) {
        this.cnpjcpfcodigo = value;
    }

    /**
     * Obtém o valor da propriedade dtemissao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDtemissao() {
        return dtemissao;
    }

    /**
     * Define o valor da propriedade dtemissao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDtemissao(String value) {
        this.dtemissao = value;
    }

    /**
     * Obtém o valor da propriedade nomearquivo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomearquivo() {
        return nomearquivo;
    }

    /**
     * Define o valor da propriedade nomearquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomearquivo(String value) {
        this.nomearquivo = value;
    }

    /**
     * Obtém o valor da propriedade extensao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensao() {
        return extensao;
    }

    /**
     * Define o valor da propriedade extensao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensao(String value) {
        this.extensao = value;
    }

    /**
     * Obtém o valor da propriedade conteudo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * Define o valor da propriedade conteudo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConteudo(String value) {
        this.conteudo = value;
    }

}
