
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

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
 *         &lt;element name="codigoAvaliacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "codigoAvaliacao"
})
@XmlRootElement(name = "buscarAvaliacaoFiltro")
public class BuscarAvaliacaoFiltro {

    protected int codigoAvaliacao;

    /**
     * Gets the value of the codigoAvaliacao property.
     * 
     */
    public int getCodigoAvaliacao() {
        return codigoAvaliacao;
    }

    /**
     * Sets the value of the codigoAvaliacao property.
     * 
     */
    public void setCodigoAvaliacao(int value) {
        this.codigoAvaliacao = value;
    }

}
