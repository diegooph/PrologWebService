
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Classe Java de ArrayOfAlternativasNokVO complex type.
 *
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 *
 * <pre>
 * &lt;complexType name="ArrayOfAlternativasNokVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AlternativasNokVO" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}AlternativasNokVO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ArrayOfAlternativasNokVO",
        propOrder = {"alternativasNokVO"})
public class ArrayOfAlternativasNokVO {

    @XmlElement(name = "AlternativasNokVO", nillable = true)
    protected List<AlternativasNokVO> alternativasNokVO;

    /**
     * Gets the value of the alternativasNokVO property.
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternativasNokVO property.
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternativasNokVO().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link AlternativasNokVO }
     */
    public List<AlternativasNokVO> getAlternativasNokVO() {
        if (alternativasNokVO == null) {
            alternativasNokVO = new ArrayList<>();
        }
        return this.alternativasNokVO;
    }
}
