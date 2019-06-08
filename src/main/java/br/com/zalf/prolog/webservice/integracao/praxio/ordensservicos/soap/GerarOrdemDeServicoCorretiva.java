
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
 *         &lt;element name="ordemDeServico" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}OrdemDeServicoCorretivaVO" minOccurs="0"/>
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
    "ordemDeServico"
})
@XmlRootElement(name = "GerarOrdemDeServicoCorretiva")
public class GerarOrdemDeServicoCorretiva {

    protected OrdemDeServicoCorretivaVO ordemDeServico;

    /**
     * Obtém o valor da propriedade ordemDeServico.
     * 
     * @return
     *     possible object is
     *     {@link OrdemDeServicoCorretivaVO }
     *     
     */
    public OrdemDeServicoCorretivaVO getOrdemDeServico() {
        return ordemDeServico;
    }

    /**
     * Define o valor da propriedade ordemDeServico.
     * 
     * @param value
     *     allowed object is
     *     {@link OrdemDeServicoCorretivaVO }
     *     
     */
    public void setOrdemDeServico(OrdemDeServicoCorretivaVO value) {
        this.ordemDeServico = value;
    }

}
