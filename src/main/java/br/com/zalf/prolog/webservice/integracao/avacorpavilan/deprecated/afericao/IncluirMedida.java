
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
 *         &lt;element name="medida" type="{http://www.avacorp.com.br/integracaoprolog}IncluirMedida" minOccurs="0"/>
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
    "medida"
})
@XmlRootElement(name = "incluirMedida")
public class IncluirMedida {

    protected IncluirMedida2 medida;

    /**
     * Gets the value of the medida property.
     * 
     * @return
     *     possible object is
     *     {@link IncluirMedida2 }
     *     
     */
    public IncluirMedida2 getMedida() {
        return medida;
    }

    /**
     * Sets the value of the medida property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncluirMedida2 }
     *     
     */
    public void setMedida(IncluirMedida2 value) {
        this.medida = value;
    }

}
