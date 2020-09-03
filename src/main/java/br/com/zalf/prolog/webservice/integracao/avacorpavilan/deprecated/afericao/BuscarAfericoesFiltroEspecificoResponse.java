
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

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
 *         &lt;element name="buscarAfericoesFiltroEspecificoResult" type="{http://www.avacorp.com.br/integracaoprologtestes}AfericoesFiltro" minOccurs="0"/>
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
    "buscarAfericoesFiltroEspecificoResult"
})
@XmlRootElement(name = "buscarAfericoesFiltroEspecificoResponse")
public class BuscarAfericoesFiltroEspecificoResponse {

    protected AfericoesFiltro buscarAfericoesFiltroEspecificoResult;

    /**
     * Gets the value of the buscarAfericoesFiltroEspecificoResult property.
     * 
     * @return
     *     possible object is
     *     {@link AfericoesFiltro }
     *     
     */
    public AfericoesFiltro getBuscarAfericoesFiltroEspecificoResult() {
        return buscarAfericoesFiltroEspecificoResult;
    }

    /**
     * Sets the value of the buscarAfericoesFiltroEspecificoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AfericoesFiltro }
     *     
     */
    public void setBuscarAfericoesFiltroEspecificoResult(AfericoesFiltro value) {
        this.buscarAfericoesFiltroEspecificoResult = value;
    }

}
