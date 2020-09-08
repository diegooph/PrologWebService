
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvacorpAvilanTipoChecklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de AdicionarChecklist complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="AdicionarChecklist">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="veiculo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoQuestionario" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dtNascimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdicionarChecklist", propOrder = {
    "veiculo",
    "codigoQuestionario",
    "cpf",
    "dtNascimento",
    "tipo"
})
public class AdicionarChecklist {

    protected String veiculo;
    protected int codigoQuestionario;
    protected String cpf;
    protected String dtNascimento;
    protected String tipo;

    /**
     * Obtém o valor da propriedade veiculo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVeiculo() {
        return veiculo;
    }

    /**
     * Define o valor da propriedade veiculo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVeiculo(String value) {
        this.veiculo = value;
    }

    /**
     * Obtém o valor da propriedade codigoQuestionario.
     * 
     */
    public int getCodigoQuestionario() {
        return codigoQuestionario;
    }

    /**
     * Define o valor da propriedade codigoQuestionario.
     * 
     */
    public void setCodigoQuestionario(int value) {
        this.codigoQuestionario = value;
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

    public AvacorpAvilanTipoChecklist getTipoChecklist() {
        return AvacorpAvilanTipoChecklist.fromString(tipo);
    }

    public void setTipoChecklist(AvacorpAvilanTipoChecklist tipoChecklist) {
        this.tipo = tipoChecklist.asString();
    }

}
