
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VeiculoQuestao complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VeiculoQuestao">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="veiculo" type="{http://www.avacorp.com.br/integracaoprolog}Veiculo" minOccurs="0"/>
 *         &lt;element name="questoes" type="{http://www.avacorp.com.br/integracaoprolog}ArrayOfQuestao" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VeiculoQuestao", propOrder = {
    "veiculo",
    "questoes"
})
public class VeiculoQuestao {

    protected Veiculo veiculo;
    protected ArrayOfQuestao questoes;

    /**
     * Gets the value of the veiculo property.
     * 
     * @return
     *     possible object is
     *     {@link Veiculo }
     *     
     */
    public Veiculo getVeiculo() {
        return veiculo;
    }

    /**
     * Sets the value of the veiculo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Veiculo }
     *     
     */
    public void setVeiculo(Veiculo value) {
        this.veiculo = value;
    }

    /**
     * Gets the value of the questoes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQuestao }
     *     
     */
    public ArrayOfQuestao getQuestoes() {
        return questoes;
    }

    /**
     * Sets the value of the questoes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQuestao }
     *     
     */
    public void setQuestoes(ArrayOfQuestao value) {
        this.questoes = value;
    }

}
