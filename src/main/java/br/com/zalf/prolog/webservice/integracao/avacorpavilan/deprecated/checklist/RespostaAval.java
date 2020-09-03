
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de RespostaAval complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="RespostaAval">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sequenciaQuestao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoResposta" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespostaAval", propOrder = {
    "sequenciaQuestao",
    "codigoResposta",
    "observacao"
})
public class RespostaAval {

    protected int sequenciaQuestao;
    protected int codigoResposta;
    protected String observacao;

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
     * Obtém o valor da propriedade codigoResposta.
     * 
     */
    public int getCodigoResposta() {
        return codigoResposta;
    }

    /**
     * Define o valor da propriedade codigoResposta.
     * 
     */
    public void setCodigoResposta(int value) {
        this.codigoResposta = value;
    }

    /**
     * Obtém o valor da propriedade observacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define o valor da propriedade observacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacao(String value) {
        this.observacao = value;
    }

}
