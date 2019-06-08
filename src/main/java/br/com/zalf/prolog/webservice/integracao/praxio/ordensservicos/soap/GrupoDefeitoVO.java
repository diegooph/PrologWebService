
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de GrupoDefeitoVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="GrupoDefeitoVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodigoGrupo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CodigoDefeito" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GrupoDefeitoVO", propOrder = {
    "codigoGrupo",
    "codigoDefeito",
    "observacao"
})
public class GrupoDefeitoVO {

    @XmlElement(name = "CodigoGrupo", required = true, type = Integer.class, nillable = true)
    protected Integer codigoGrupo;
    @XmlElement(name = "CodigoDefeito")
    protected int codigoDefeito;
    @XmlElement(name = "Observacao")
    protected String observacao;

    /**
     * Obtém o valor da propriedade codigoGrupo.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodigoGrupo() {
        return codigoGrupo;
    }

    /**
     * Define o valor da propriedade codigoGrupo.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodigoGrupo(Integer value) {
        this.codigoGrupo = value;
    }

    /**
     * Obtém o valor da propriedade codigoDefeito.
     * 
     */
    public int getCodigoDefeito() {
        return codigoDefeito;
    }

    /**
     * Define o valor da propriedade codigoDefeito.
     * 
     */
    public void setCodigoDefeito(int value) {
        this.codigoDefeito = value;
    }

    /**
     * Obtém o valor da propriedade observacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define o valor da propriedade observacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacao(String value) {
        this.observacao = value;
    }

}
