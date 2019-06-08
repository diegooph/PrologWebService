
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
 *         &lt;element name="GerarOrdemDeServicoCorretivaPrologResult" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}RetornoOsCorretivaVO" minOccurs="0"/>
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
    "gerarOrdemDeServicoCorretivaPrologResult"
})
@XmlRootElement(name = "GerarOrdemDeServicoCorretivaPrologResponse")
public class GerarOrdemDeServicoCorretivaPrologResponse {

    @XmlElement(name = "GerarOrdemDeServicoCorretivaPrologResult")
    protected RetornoOsCorretivaVO gerarOrdemDeServicoCorretivaPrologResult;

    /**
     * Obtém o valor da propriedade gerarOrdemDeServicoCorretivaPrologResult.
     * 
     * @return
     *     possible object is
     *     {@link RetornoOsCorretivaVO }
     *     
     */
    public RetornoOsCorretivaVO getGerarOrdemDeServicoCorretivaPrologResult() {
        return gerarOrdemDeServicoCorretivaPrologResult;
    }

    /**
     * Define o valor da propriedade gerarOrdemDeServicoCorretivaPrologResult.
     * 
     * @param value
     *     allowed object is
     *     {@link RetornoOsCorretivaVO }
     *     
     */
    public void setGerarOrdemDeServicoCorretivaPrologResult(RetornoOsCorretivaVO value) {
        this.gerarOrdemDeServicoCorretivaPrologResult = value;
    }

}
