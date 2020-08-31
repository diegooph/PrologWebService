
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de RespostasAvaliacao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
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
     * Obtém o valor da propriedade odometro.
     * 
     */
    public int getOdometro() {
        return odometro;
    }

    /**
     * Define o valor da propriedade odometro.
     * 
     */
    public void setOdometro(int value) {
        this.odometro = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
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
     * Define o valor da propriedade cpf.
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
     * Obtém o valor da propriedade dtNascimento.
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
     * Define o valor da propriedade dtNascimento.
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
     * Obtém o valor da propriedade codigoAvaliacao.
     * 
     */
    public int getCodigoAvaliacao() {
        return codigoAvaliacao;
    }

    /**
     * Define o valor da propriedade codigoAvaliacao.
     * 
     */
    public void setCodigoAvaliacao(int value) {
        this.codigoAvaliacao = value;
    }

    /**
     * Obtém o valor da propriedade respostas.
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
     * Define o valor da propriedade respostas.
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
