
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de RetornoEnvioPrologVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="RetornoEnvioPrologVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msgErro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoErroProlog" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="httpCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="msgDev" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgRetornoOk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetornoEnvioPrologVO", propOrder = {
    "msgErro",
    "codigoErroProlog",
    "httpCode",
    "msgDev",
    "msgRetornoOk"
})
public class RetornoEnvioPrologVO {

    protected String msgErro;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer codigoErroProlog;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer httpCode;
    protected String msgDev;
    protected String msgRetornoOk;

    /**
     * Obtém o valor da propriedade msgErro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgErro() {
        return msgErro;
    }

    /**
     * Define o valor da propriedade msgErro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgErro(String value) {
        this.msgErro = value;
    }

    /**
     * Obtém o valor da propriedade codigoErroProlog.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodigoErroProlog() {
        return codigoErroProlog;
    }

    /**
     * Define o valor da propriedade codigoErroProlog.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodigoErroProlog(Integer value) {
        this.codigoErroProlog = value;
    }

    /**
     * Obtém o valor da propriedade httpCode.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHttpCode() {
        return httpCode;
    }

    /**
     * Define o valor da propriedade httpCode.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHttpCode(Integer value) {
        this.httpCode = value;
    }

    /**
     * Obtém o valor da propriedade msgDev.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgDev() {
        return msgDev;
    }

    /**
     * Define o valor da propriedade msgDev.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgDev(String value) {
        this.msgDev = value;
    }

    /**
     * Obtém o valor da propriedade msgRetornoOk.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgRetornoOk() {
        return msgRetornoOk;
    }

    /**
     * Define o valor da propriedade msgRetornoOk.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgRetornoOk(String value) {
        this.msgRetornoOk = value;
    }

}
