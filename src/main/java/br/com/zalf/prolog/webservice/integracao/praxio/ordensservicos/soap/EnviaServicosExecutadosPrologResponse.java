
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
 *         &lt;element name="EnviaServicosExecutadosPrologResult" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}RetornoEnvioPrologVO" minOccurs="0"/>
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
    "enviaServicosExecutadosPrologResult"
})
@XmlRootElement(name = "EnviaServicosExecutadosPrologResponse")
public class EnviaServicosExecutadosPrologResponse {

    @XmlElement(name = "EnviaServicosExecutadosPrologResult")
    protected RetornoEnvioPrologVO enviaServicosExecutadosPrologResult;

    /**
     * Obtém o valor da propriedade enviaServicosExecutadosPrologResult.
     * 
     * @return
     *     possible object is
     *     {@link RetornoEnvioPrologVO }
     *     
     */
    public RetornoEnvioPrologVO getEnviaServicosExecutadosPrologResult() {
        return enviaServicosExecutadosPrologResult;
    }

    /**
     * Define o valor da propriedade enviaServicosExecutadosPrologResult.
     * 
     * @param value
     *     allowed object is
     *     {@link RetornoEnvioPrologVO }
     *     
     */
    public void setEnviaServicosExecutadosPrologResult(RetornoEnvioPrologVO value) {
        this.enviaServicosExecutadosPrologResult = value;
    }

}
