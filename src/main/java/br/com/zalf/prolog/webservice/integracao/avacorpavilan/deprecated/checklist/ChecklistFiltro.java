
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvacorpAvilanTipoChecklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChecklistFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChecklistFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoQuestionario" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoChecklist" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="colaborador" type="{http://www.avacorp.com.br/integracaoprologtestes}Colaborador" minOccurs="0"/>
 *         &lt;element name="dataHoraRealizacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="odometro" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="quantidadeRespostasOk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="quantidadeRespostasNaoOk" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tempoRealizacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avaliacao" type="{http://www.avacorp.com.br/integracaoprologtestes}AvaliacaoFiltro" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChecklistFiltro", propOrder = {
    "codigoQuestionario",
    "codigoChecklist",
    "colaborador",
    "dataHoraRealizacao",
    "placa",
    "odometro",
    "quantidadeRespostasOk",
    "quantidadeRespostasNaoOk",
    "tempoRealizacao",
    "tipo",
    "avaliacao"
})
public class ChecklistFiltro {

    protected int codigoQuestionario;
    protected int codigoChecklist;
    protected ColaboradorAvilan colaborador;
    protected String dataHoraRealizacao;
    protected String placa;
    protected int odometro;
    protected int quantidadeRespostasOk;
    protected int quantidadeRespostasNaoOk;
    protected String tempoRealizacao;
    protected String tipo;
    protected AvaliacaoFiltro avaliacao;

    /**
     * Gets the value of the codigoQuestionario property.
     * 
     */
    public int getCodigoQuestionario() {
        return codigoQuestionario;
    }

    /**
     * Sets the value of the codigoQuestionario property.
     * 
     */
    public void setCodigoQuestionario(int value) {
        this.codigoQuestionario = value;
    }

    /**
     * Gets the value of the codigoChecklist property.
     * 
     */
    public int getCodigoChecklist() {
        return codigoChecklist;
    }

    /**
     * Sets the value of the codigoChecklist property.
     * 
     */
    public void setCodigoChecklist(int value) {
        this.codigoChecklist = value;
    }

    /**
     * Gets the value of the colaborador property.
     * 
     * @return
     *     possible object is
     *     {@link ColaboradorAvilan }
     *     
     */
    public ColaboradorAvilan getColaborador() {
        return colaborador;
    }

    /**
     * Sets the value of the colaborador property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColaboradorAvilan }
     *     
     */
    public void setColaborador(ColaboradorAvilan value) {
        this.colaborador = value;
    }

    /**
     * Gets the value of the dataHoraRealizacao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    /**
     * Sets the value of the dataHoraRealizacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataHoraRealizacao(String value) {
        this.dataHoraRealizacao = value;
    }

    /**
     * Gets the value of the placa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Sets the value of the placa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaca(String value) {
        this.placa = value;
    }

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
     * Gets the value of the quantidadeRespostasOk property.
     * 
     */
    public int getQuantidadeRespostasOk() {
        return quantidadeRespostasOk;
    }

    /**
     * Sets the value of the quantidadeRespostasOk property.
     * 
     */
    public void setQuantidadeRespostasOk(int value) {
        this.quantidadeRespostasOk = value;
    }

    /**
     * Gets the value of the quantidadeRespostasNaoOk property.
     * 
     */
    public int getQuantidadeRespostasNaoOk() {
        return quantidadeRespostasNaoOk;
    }

    /**
     * Sets the value of the quantidadeRespostasNaoOk property.
     * 
     */
    public void setQuantidadeRespostasNaoOk(int value) {
        this.quantidadeRespostasNaoOk = value;
    }

    /**
     * Gets the value of the tempoRealizacao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTempoRealizacao() {
        return tempoRealizacao;
    }

    /**
     * Sets the value of the tempoRealizacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTempoRealizacao(String value) {
        this.tempoRealizacao = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public AvacorpAvilanTipoChecklist getTipo() {
        return AvacorpAvilanTipoChecklist.fromString(tipo);
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

    /**
     * Gets the value of the avaliacao property.
     * 
     * @return
     *     possible object is
     *     {@link AvaliacaoFiltro }
     *     
     */
    public AvaliacaoFiltro getAvaliacao() {
        return avaliacao;
    }

    /**
     * Sets the value of the avaliacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvaliacaoFiltro }
     *     
     */
    public void setAvaliacao(AvaliacaoFiltro value) {
        this.avaliacao = value;
    }

}
