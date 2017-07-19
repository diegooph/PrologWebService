
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the tipodocumento property.
     * 
     */
    public int getTipodocumento() {
        return tipodocumento;
    }

    /**
     * Sets the value of the tipodocumento property.
     * 
     */
    public void setTipodocumento(int value) {
        this.tipodocumento = value;
    }

    /**
     * Gets the value of the diferenciadornumero property.
     * 
     */
    public int getDiferenciadornumero() {
        return diferenciadornumero;
    }

    /**
     * Sets the value of the diferenciadornumero property.
     * 
     */
    public void setDiferenciadornumero(int value) {
        this.diferenciadornumero = value;
    }

    /**
     * Gets the value of the serie property.
     * 
     */
    public int getSerie() {
        return serie;
    }

    /**
     * Sets the value of the serie property.
     * 
     */
    public void setSerie(int value) {
        this.serie = value;
    }

    /**
     * Gets the value of the numerosequencia property.
     * 
     */
    public int getNumerosequencia() {
        return numerosequencia;
    }

    /**
     * Sets the value of the numerosequencia property.
     * 
     */
    public void setNumerosequencia(int value) {
        this.numerosequencia = value;
    }

    /**
     * Gets the value of the numerosequenciaitem property.
     * 
     */
    public int getNumerosequenciaitem() {
        return numerosequenciaitem;
    }

    /**
     * Sets the value of the numerosequenciaitem property.
     * 
     */
    public void setNumerosequenciaitem(int value) {
        this.numerosequenciaitem = value;
    }

    /**
     * Gets the value of the cnpjcpfcodigo property.
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
     * Sets the value of the cnpjcpfcodigo property.
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
     * Gets the value of the dtemissao property.
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
     * Sets the value of the dtemissao property.
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
     * Gets the value of the nomearquivo property.
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
     * Sets the value of the nomearquivo property.
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
     * Gets the value of the extensao property.
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
     * Sets the value of the extensao property.
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
     * Gets the value of the conteudo property.
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
     * Sets the value of the conteudo property.
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
