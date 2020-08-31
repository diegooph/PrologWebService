
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester.AvacorpAvilanRequestStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de PerguntasAlternativasQuestionario complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="PerguntasAlternativasQuestionario">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="veiculoQuestoes" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfVeiculoQuestao" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PerguntasAlternativasQuestionario", propOrder = {
    "sucesso",
    "mensagem",
    "veiculoQuestoes"
})
public class PerguntasAlternativasQuestionario implements AvacorpAvilanRequestStatus {

    protected boolean sucesso;
    protected String mensagem;
    protected ArrayOfVeiculoQuestao veiculoQuestoes;

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
     * Obtém o valor da propriedade veiculoQuestoes.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVeiculoQuestao }
     *     
     */
    public ArrayOfVeiculoQuestao getVeiculoQuestoes() {
        return veiculoQuestoes;
    }

    /**
     * Define o valor da propriedade veiculoQuestoes.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVeiculoQuestao }
     *     
     */
    public void setVeiculoQuestoes(ArrayOfVeiculoQuestao value) {
        this.veiculoQuestoes = value;
    }

}
