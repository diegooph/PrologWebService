
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AvaliacaoFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AvaliacaoFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="questoes" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfQuestaoFiltro" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AvaliacaoFiltro", propOrder = {
    "questoes"
})
public class AvaliacaoFiltro {

    protected ArrayOfQuestaoFiltro questoes;

    /**
     * Gets the value of the questoes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQuestaoFiltro }
     *     
     */
    public ArrayOfQuestaoFiltro getQuestoes() {
        return questoes;
    }

    /**
     * Sets the value of the questoes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQuestaoFiltro }
     *     
     */
    public void setQuestoes(ArrayOfQuestaoFiltro value) {
        this.questoes = value;
    }

}
