
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfResposta complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfResposta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Resposta" type="{http://www.avacorp.com.br/integracaoprolog}Resposta" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfResposta", propOrder = {
    "resposta"
})
public class ArrayOfResposta {

    @XmlElement(name = "Resposta", nillable = true)
    protected List<Resposta> resposta;

    /**
     * Gets the value of the resposta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resposta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResposta().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resposta }
     * 
     * 
     */
    public List<Resposta> getResposta() {
        if (resposta == null) {
            resposta = new ArrayList<Resposta>();
        }
        return this.resposta;
    }

}
