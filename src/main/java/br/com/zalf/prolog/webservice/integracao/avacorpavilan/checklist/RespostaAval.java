
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RespostaAval complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the codigoResposta property.
     * 
     */
    public int getCodigoResposta() {
        return codigoResposta;
    }

    /**
     * Sets the value of the codigoResposta property.
     * 
     */
    public void setCodigoResposta(int value) {
        this.codigoResposta = value;
    }

    /**
     * Gets the value of the observacao property.
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
     * Sets the value of the observacao property.
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
