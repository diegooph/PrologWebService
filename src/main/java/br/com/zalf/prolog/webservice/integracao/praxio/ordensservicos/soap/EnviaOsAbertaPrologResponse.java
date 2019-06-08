
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="EnviaOsAbertaPrologResult" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}RetornoEnvioPrologVO" minOccurs="0"/>
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
    "enviaOsAbertaPrologResult"
})
@XmlRootElement(name = "EnviaOsAbertaPrologResponse")
public class EnviaOsAbertaPrologResponse {

    @XmlElement(name = "EnviaOsAbertaPrologResult")
    protected RetornoEnvioPrologVO enviaOsAbertaPrologResult;

    /**
     * Obtém o valor da propriedade enviaOsAbertaPrologResult.
     * 
     * @return
     *     possible object is
     *     {@link RetornoEnvioPrologVO }
     *     
     */
    public RetornoEnvioPrologVO getEnviaOsAbertaPrologResult() {
        return enviaOsAbertaPrologResult;
    }

    /**
     * Define o valor da propriedade enviaOsAbertaPrologResult.
     * 
     * @param value
     *     allowed object is
     *     {@link RetornoEnvioPrologVO }
     *     
     */
    public void setEnviaOsAbertaPrologResult(RetornoEnvioPrologVO value) {
        this.enviaOsAbertaPrologResult = value;
    }

}
