
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QuestionarioVeiculos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
     * Gets the value of the questionario property.
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
     * Sets the value of the questionario property.
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
     * Gets the value of the veiculos property.
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
     * Sets the value of the veiculos property.
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
