
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de QuestionarioVeiculos complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="QuestionarioVeiculos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="questionario" type="{http://www.avacorp.com.br/integracaoprolog}Questionario" minOccurs="0"/>
 *         &lt;element name="veiculos" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfVeiculo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestionarioVeiculos", propOrder = {
    "questionario",
    "veiculos"
})
public class QuestionarioVeiculos {

    protected Questionario questionario;
    protected ArrayOfVeiculo veiculos;

    /**
     * Obtém o valor da propriedade questionario.
     * 
     * @return
     *     possible object is
     *     {@link Questionario }
     *     
     */
    public Questionario getQuestionario() {
        return questionario;
    }

    /**
     * Define o valor da propriedade questionario.
     * 
     * @param value
     *     allowed object is
     *     {@link Questionario }
     *     
     */
    public void setQuestionario(Questionario value) {
        this.questionario = value;
    }

    /**
     * Obtém o valor da propriedade veiculos.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVeiculo }
     *     
     */
    public ArrayOfVeiculo getVeiculos() {
        return veiculos;
    }

    /**
     * Define o valor da propriedade veiculos.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVeiculo }
     *     
     */
    public void setVeiculos(ArrayOfVeiculo value) {
        this.veiculos = value;
    }

}
