
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvacorpAvilanRequestStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VeiculoTipo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VeiculoTipo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="veiculos" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VeiculoTipo", propOrder = {
    "sucesso",
    "mensagem",
    "veiculos"
})
public class VeiculoTipo implements AvacorpAvilanRequestStatus {

    protected boolean sucesso;
    protected String mensagem;
    protected ArrayOfString veiculos;

    /**
     * Gets the value of the sucesso property.
     * 
     */
    public boolean isSucesso() {
        return sucesso;
    }

    /**
     * Sets the value of the sucesso property.
     * 
     */
    public void setSucesso(boolean value) {
        this.sucesso = value;
    }

    /**
     * Gets the value of the mensagem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Sets the value of the mensagem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensagem(String value) {
        this.mensagem = value;
    }

    /**
     * Gets the value of the veiculos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getVeiculos() {
        return veiculos;
    }

    /**
     * Sets the value of the veiculos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setVeiculos(ArrayOfString value) {
        this.veiculos = value;
    }

}
