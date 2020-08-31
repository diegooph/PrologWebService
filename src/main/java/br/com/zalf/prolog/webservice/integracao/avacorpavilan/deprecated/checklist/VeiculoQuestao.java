
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de VeiculoQuestao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
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
     * Obtém o valor da propriedade veiculo.
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
     * Define o valor da propriedade veiculo.
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
     * Obtém o valor da propriedade questoes.
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
     * Define o valor da propriedade questoes.
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
