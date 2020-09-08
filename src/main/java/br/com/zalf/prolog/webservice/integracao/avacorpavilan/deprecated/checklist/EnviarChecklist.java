
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

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
 *         &lt;element name="respostas" type="{http://www.avacorp.com.br/integracaoprolog}RespostasAvaliacao" minOccurs="0"/>
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
    "respostas"
})
@XmlRootElement(name = "enviarChecklist")
public class EnviarChecklist {

    protected RespostasAvaliacao respostas;

    /**
     * Obtém o valor da propriedade respostas.
     * 
     * @return
     *     possible object is
     *     {@link RespostasAvaliacao }
     *     
     */
    public RespostasAvaliacao getRespostas() {
        return respostas;
    }

    /**
     * Define o valor da propriedade respostas.
     * 
     * @param value
     *     allowed object is
     *     {@link RespostasAvaliacao }
     *     
     */
    public void setRespostas(RespostasAvaliacao value) {
        this.respostas = value;
    }

}
