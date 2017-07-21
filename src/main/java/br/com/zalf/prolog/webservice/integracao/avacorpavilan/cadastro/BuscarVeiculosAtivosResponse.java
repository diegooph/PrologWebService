
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro;

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
 *         &lt;element name="buscarVeiculosAtivosResult" type="{http://www.avacorp.com.br/integracaoprolog}VeiculosAtivos" minOccurs="0"/>
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
    "buscarVeiculosAtivosResult"
})
@XmlRootElement(name = "buscarVeiculosAtivosResponse")
public class BuscarVeiculosAtivosResponse {

    protected VeiculosAtivos buscarVeiculosAtivosResult;

    /**
     * Obtém o valor da propriedade buscarVeiculosAtivosResult.
     * 
     * @return
     *     possible object is
     *     {@link VeiculosAtivos }
     *     
     */
    public VeiculosAtivos getBuscarVeiculosAtivosResult() {
        return buscarVeiculosAtivosResult;
    }

    /**
     * Define o valor da propriedade buscarVeiculosAtivosResult.
     * 
     * @param value
     *     allowed object is
     *     {@link VeiculosAtivos }
     *     
     */
    public void setBuscarVeiculosAtivosResult(VeiculosAtivos value) {
        this.buscarVeiculosAtivosResult = value;
    }

}
