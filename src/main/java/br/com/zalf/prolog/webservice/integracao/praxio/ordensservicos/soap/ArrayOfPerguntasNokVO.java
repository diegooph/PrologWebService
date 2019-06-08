
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfPerguntasNokVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfPerguntasNokVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PerguntasNokVO" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}PerguntasNokVO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfPerguntasNokVO", propOrder = {
    "perguntasNokVO"
})
public class ArrayOfPerguntasNokVO {

    @XmlElement(name = "PerguntasNokVO", nillable = true)
    protected List<PerguntasNokVO> perguntasNokVO;

    /**
     * Gets the value of the perguntasNokVO property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the perguntasNokVO property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPerguntasNokVO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PerguntasNokVO }
     * 
     * 
     */
    public List<PerguntasNokVO> getPerguntasNokVO() {
        if (perguntasNokVO == null) {
            perguntasNokVO = new ArrayList<PerguntasNokVO>();
        }
        return this.perguntasNokVO;
    }

}
