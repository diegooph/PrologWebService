
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de VeiculosAtivos complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
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
public class VeiculosAtivos {

    protected boolean sucesso;
    protected String mensagem;
    @XmlElement(name = "ListaVeiculos")
    protected ArrayOfVeiculo listaVeiculos;

    /**
     * Obtém o valor da propriedade sucesso.
     * 
     */
    public boolean isSucesso() {
        return sucesso;
    }

    /**
     * Define o valor da propriedade sucesso.
     * 
     */
    public void setSucesso(boolean value) {
        this.sucesso = value;
    }

    /**
     * Obtém o valor da propriedade mensagem.
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
     * Define o valor da propriedade mensagem.
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
     * Obtém o valor da propriedade listaVeiculos.
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
     * Define o valor da propriedade listaVeiculos.
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
