
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester.AvacorpAvilanRequestStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VeiculosAtivos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VeiculosAtivos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListaVeiculos" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfVeiculo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VeiculosAtivos", propOrder = {
    "sucesso",
    "mensagem",
    "listaVeiculos"
})
public class VeiculosAtivos implements AvacorpAvilanRequestStatus {

    protected boolean sucesso;
    protected String mensagem;
    @XmlElement(name = "ListaVeiculos")
    protected ArrayOfVeiculo listaVeiculos;

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
     * Gets the value of the listaVeiculos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVeiculo }
     *     
     */
    public ArrayOfVeiculo getListaVeiculos() {
        return listaVeiculos;
    }

    /**
     * Sets the value of the listaVeiculos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVeiculo }
     *     
     */
    public void setListaVeiculos(ArrayOfVeiculo value) {
        this.listaVeiculos = value;
    }
}
