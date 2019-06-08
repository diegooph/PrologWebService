
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ArrayOfGrupoDefeitoVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGrupoDefeitoVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrupoDefeitoVO" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}GrupoDefeitoVO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGrupoDefeitoVO", propOrder = {
    "grupoDefeitoVO"
})
public class ArrayOfGrupoDefeitoVO {

    @XmlElement(name = "GrupoDefeitoVO", nillable = true)
    protected List<GrupoDefeitoVO> grupoDefeitoVO;

    /**
     * Gets the value of the grupoDefeitoVO property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the grupoDefeitoVO property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGrupoDefeitoVO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GrupoDefeitoVO }
     * 
     * 
     */
    public List<GrupoDefeitoVO> getGrupoDefeitoVO() {
        if (grupoDefeitoVO == null) {
            grupoDefeitoVO = new ArrayList<GrupoDefeitoVO>();
        }
        return this.grupoDefeitoVO;
    }

}
