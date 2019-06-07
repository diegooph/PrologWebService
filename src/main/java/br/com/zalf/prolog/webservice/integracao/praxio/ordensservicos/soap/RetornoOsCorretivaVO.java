
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de RetornoOsCorretivaVO complex type.
 *
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 *
 * <pre>
 * &lt;complexType name="RetornoOsCorretivaVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="MensagemDeRetorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CodigoOS" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RetornoOsCorretivaVO",
        propOrder = {"sucesso",
                "mensagemDeRetorno",
                "codigoOS"})
public class RetornoOsCorretivaVO {

    @XmlElement(name = "Sucesso")
    protected boolean sucesso;
    @XmlElement(name = "MensagemDeRetorno")
    protected String mensagemDeRetorno;
    @XmlElement(name = "CodigoOS")
    protected int codigoOS;

    /**
     * Obtém o valor da propriedade sucesso.
     */
    public boolean isSucesso() {
        return sucesso;
    }

    /**
     * Define o valor da propriedade sucesso.
     */
    public void setSucesso(boolean value) {
        this.sucesso = value;
    }

    /**
     * Obtém o valor da propriedade mensagemDeRetorno.
     *
     * @return possible object is {@link String }
     */
    public String getMensagemDeRetorno() {
        return mensagemDeRetorno;
    }

    /**
     * Define o valor da propriedade mensagemDeRetorno.
     *
     * @param value allowed object is {@link String }
     */
    public void setMensagemDeRetorno(String value) {
        this.mensagemDeRetorno = value;
    }

    /**
     * Obtém o valor da propriedade codigoOS.
     */
    public int getCodigoOS() {
        return codigoOS;
    }

    /**
     * Define o valor da propriedade codigoOS.
     */
    public void setCodigoOS(int value) {
        this.codigoOS = value;
    }
}
