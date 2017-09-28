
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.farol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FarolChecklist complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FarolChecklist">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="farolDia" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfFarolDia" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FarolChecklist", propOrder = {
    "mensagem",
    "sucesso",
    "farolDia"
})
public class FarolChecklist2 {

    protected String mensagem;
    protected boolean sucesso;
    protected ArrayOfFarolDia farolDia;

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
     * Gets the value of the farolDia property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFarolDia }
     *     
     */
    public ArrayOfFarolDia getFarolDia() {
        return farolDia;
    }

    /**
     * Sets the value of the farolDia property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFarolDia }
     *     
     */
    public void setFarolDia(ArrayOfFarolDia value) {
        this.farolDia = value;
    }

}
