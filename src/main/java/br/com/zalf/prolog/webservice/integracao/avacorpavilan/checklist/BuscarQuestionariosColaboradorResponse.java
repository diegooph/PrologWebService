
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="buscarQuestionariosColaboradorResult" type="{http://www.avacorp.com.br/integracaoprolog}BuscaQuestionarioColaborador" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "buscarQuestionariosColaboradorResult"
})
@XmlRootElement(name = "buscarQuestionariosColaboradorResponse")
public class BuscarQuestionariosColaboradorResponse {

    protected BuscaQuestionarioColaborador buscarQuestionariosColaboradorResult;

    /**
     * Gets the value of the buscarQuestionariosColaboradorResult property.
     * 
     * @return
     *     possible object is
     *     {@link BuscaQuestionarioColaborador }
     *     
     */
    public BuscaQuestionarioColaborador getBuscarQuestionariosColaboradorResult() {
        return buscarQuestionariosColaboradorResult;
    }

    /**
     * Sets the value of the buscarQuestionariosColaboradorResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BuscaQuestionarioColaborador }
     *     
     */
    public void setBuscarQuestionariosColaboradorResult(BuscaQuestionarioColaborador value) {
        this.buscarQuestionariosColaboradorResult = value;
    }

}
