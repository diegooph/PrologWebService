
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoOS" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "codigoOS"
})
@XmlRootElement(name = "EnviaServicosExecutadosProlog")
public class EnviaServicosExecutadosProlog {

    protected int codigoOS;

    /**
     * Obtém o valor da propriedade codigoOS.
     * 
     */
    public int getCodigoOS() {
        return codigoOS;
    }

    /**
     * Define o valor da propriedade codigoOS.
     * 
     */
    public void setCodigoOS(int value) {
        this.codigoOS = value;
    }

}
