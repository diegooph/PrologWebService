
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester.AvacorpAvilanRequestStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de BuscaQuestionarioColaborador complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="BuscaQuestionarioColaborador">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="questionarioVeiculos" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfQuestionarioVeiculos" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuscaQuestionarioColaborador", propOrder = {
    "sucesso",
    "mensagem",
    "questionarioVeiculos"
})
public class BuscaQuestionarioColaborador implements AvacorpAvilanRequestStatus {

    protected boolean sucesso;
    protected String mensagem;
    protected ArrayOfQuestionarioVeiculos questionarioVeiculos;

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
     * Obtém o valor da propriedade questionarioVeiculos.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQuestionarioVeiculos }
     *     
     */
    public ArrayOfQuestionarioVeiculos getQuestionarioVeiculos() {
        return questionarioVeiculos;
    }

    /**
     * Define o valor da propriedade questionarioVeiculos.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQuestionarioVeiculos }
     *     
     */
    public void setQuestionarioVeiculos(ArrayOfQuestionarioVeiculos value) {
        this.questionarioVeiculos = value;
    }

}
