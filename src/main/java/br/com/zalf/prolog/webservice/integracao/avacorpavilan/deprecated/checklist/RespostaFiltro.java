
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RespostaFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RespostaFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alternativas" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfAlternativaFiltro" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespostaFiltro", propOrder = {
    "observacao",
    "alternativas"
})
public class RespostaFiltro {

    protected String observacao;
    protected ArrayOfAlternativaFiltro alternativas;

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

    /**
     * Gets the value of the alternativas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAlternativaFiltro }
     *     
     */
    public ArrayOfAlternativaFiltro getAlternativas() {
        return alternativas;
    }

    /**
     * Sets the value of the alternativas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAlternativaFiltro }
     *     
     */
    public void setAlternativas(ArrayOfAlternativaFiltro value) {
        this.alternativas = value;
    }

}
