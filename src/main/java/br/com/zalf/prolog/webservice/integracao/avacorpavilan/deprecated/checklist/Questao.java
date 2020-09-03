
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanTipoResposta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Questao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
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
     * Obtém o valor da propriedade sequenciaQuestao.
     * 
     */
    public int getSequenciaQuestao() {
        return sequenciaQuestao;
    }

    /**
     * Define o valor da propriedade sequenciaQuestao.
     * 
     */
    public void setSequenciaQuestao(int value) {
        this.sequenciaQuestao = value;
    }

    /**
     * Obtém o valor da propriedade tipoResposta.
     * 
     */
    public AvaCorpAvilanTipoResposta getTipoResposta() {
        return AvaCorpAvilanTipoResposta.fromInt(tipoResposta);
    }

    /**
     * Define o valor da propriedade tipoResposta.
     * 
     */
    public void setTipoResposta(int value) {
        this.tipoResposta = value;
    }

    /**
     * Obtém o valor da propriedade complemento.
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
     * Define o valor da propriedade complemento.
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
     * Obtém o valor da propriedade descricao.
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
     * Define o valor da propriedade descricao.
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
     * Obtém o valor da propriedade obrigatorioObservacao.
     * 
     */
    public int getObrigatorioObservacao() {
        return obrigatorioObservacao;
    }

    /**
     * Define o valor da propriedade obrigatorioObservacao.
     * 
     */
    public void setObrigatorioObservacao(int value) {
        this.obrigatorioObservacao = value;
    }

    /**
     * Obtém o valor da propriedade obrigatorioImagem.
     * 
     */
    public int getObrigatorioImagem() {
        return obrigatorioImagem;
    }

    /**
     * Define o valor da propriedade obrigatorioImagem.
     * 
     */
    public void setObrigatorioImagem(int value) {
        this.obrigatorioImagem = value;
    }

    /**
     * Obtém o valor da propriedade agrupamento.
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
     * Define o valor da propriedade agrupamento.
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
     * Obtém o valor da propriedade respostas.
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
     * Define o valor da propriedade respostas.
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
