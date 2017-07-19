
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RespostasAvaliacao complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RespostasAvaliacao">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="odometro" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dtNascimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoAvaliacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="respostas" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfRespostaAval" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespostasAvaliacao", propOrder = {
    "odometro",
    "cpf",
    "dtNascimento",
    "codigoAvaliacao",
    "respostas"
})
public class RespostasAvaliacao {

    protected int odometro;
    protected String cpf;
    protected String dtNascimento;
    protected int codigoAvaliacao;
    protected ArrayOfRespostaAval respostas;

    /**
     * Gets the value of the odometro property.
     * 
     */
    public int getOdometro() {
        return odometro;
    }

    /**
     * Sets the value of the odometro property.
     * 
     */
    public void setOdometro(int value) {
        this.odometro = value;
    }

    /**
     * Gets the value of the cpf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Sets the value of the cpf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpf(String value) {
        this.cpf = value;
    }

    /**
     * Gets the value of the dtNascimento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDtNascimento() {
        return dtNascimento;
    }

    /**
     * Sets the value of the dtNascimento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDtNascimento(String value) {
        this.dtNascimento = value;
    }

    /**
     * Gets the value of the codigoAvaliacao property.
     * 
     */
    public int getCodigoAvaliacao() {
        return codigoAvaliacao;
    }

    /**
     * Sets the value of the codigoAvaliacao property.
     * 
     */
    public void setCodigoAvaliacao(int value) {
        this.codigoAvaliacao = value;
    }

    /**
     * Gets the value of the respostas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRespostaAval }
     *     
     */
    public ArrayOfRespostaAval getRespostas() {
        return respostas;
    }

    /**
     * Sets the value of the respostas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRespostaAval }
     *     
     */
    public void setRespostas(ArrayOfRespostaAval value) {
        this.respostas = value;
    }

}
