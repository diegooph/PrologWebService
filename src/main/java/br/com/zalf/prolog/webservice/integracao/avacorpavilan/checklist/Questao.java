
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Questao complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Questao">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoAvaliacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sequenciaQuestao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tipoResposta" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obrigatorioObservacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="obrigatorioImagem" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="agrupamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="respostas" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfResposta" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Questao", propOrder = {
    "codigoAvaliacao",
    "sequenciaQuestao",
    "tipoResposta",
    "complemento",
    "descricao",
    "obrigatorioObservacao",
    "obrigatorioImagem",
    "agrupamento",
    "respostas"
})
public class Questao {

    protected int codigoAvaliacao;
    protected int sequenciaQuestao;
    protected int tipoResposta;
    protected String complemento;
    protected String descricao;
    protected int obrigatorioObservacao;
    protected int obrigatorioImagem;
    protected String agrupamento;
    protected ArrayOfResposta respostas;

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
     * Gets the value of the sequenciaQuestao property.
     * 
     */
    public int getSequenciaQuestao() {
        return sequenciaQuestao;
    }

    /**
     * Sets the value of the sequenciaQuestao property.
     * 
     */
    public void setSequenciaQuestao(int value) {
        this.sequenciaQuestao = value;
    }

    /**
     * Gets the value of the tipoResposta property.
     * 
     */
    public int getTipoResposta() {
        return tipoResposta;
    }

    /**
     * Sets the value of the tipoResposta property.
     * 
     */
    public void setTipoResposta(int value) {
        this.tipoResposta = value;
    }

    /**
     * Gets the value of the complemento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * Sets the value of the complemento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplemento(String value) {
        this.complemento = value;
    }

    /**
     * Gets the value of the descricao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Sets the value of the descricao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescricao(String value) {
        this.descricao = value;
    }

    /**
     * Gets the value of the obrigatorioObservacao property.
     * 
     */
    public int getObrigatorioObservacao() {
        return obrigatorioObservacao;
    }

    /**
     * Sets the value of the obrigatorioObservacao property.
     * 
     */
    public void setObrigatorioObservacao(int value) {
        this.obrigatorioObservacao = value;
    }

    /**
     * Gets the value of the obrigatorioImagem property.
     * 
     */
    public int getObrigatorioImagem() {
        return obrigatorioImagem;
    }

    /**
     * Sets the value of the obrigatorioImagem property.
     * 
     */
    public void setObrigatorioImagem(int value) {
        this.obrigatorioImagem = value;
    }

    /**
     * Gets the value of the agrupamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgrupamento() {
        return agrupamento;
    }

    /**
     * Sets the value of the agrupamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgrupamento(String value) {
        this.agrupamento = value;
    }

    /**
     * Gets the value of the respostas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfResposta }
     *     
     */
    public ArrayOfResposta getRespostas() {
        return respostas;
    }

    /**
     * Sets the value of the respostas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfResposta }
     *     
     */
    public void setRespostas(ArrayOfResposta value) {
        this.respostas = value;
    }

}
