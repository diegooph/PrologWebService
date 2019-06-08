
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Classe Java de PerguntasNokVO complex type.
 * <p>
 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <pre>
 * &lt;complexType name="PerguntasNokVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodPerguntaNok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DescricaoPerguntaNok" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListaAlternativasNok" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}ArrayOfAlternativasNokVO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PerguntasNokVO",
        propOrder = {
                "codPerguntaNok",
                "descricaoPerguntaNok",
                "listaAlternativasNok"})
public class PerguntasNokVO {

    @XmlElement(name = "CodPerguntaNok")
    protected int codPerguntaNok;
    @XmlElement(name = "DescricaoPerguntaNok")
    protected String descricaoPerguntaNok;
    @XmlElement(name = "ListaAlternativasNok")
    protected ArrayOfAlternativasNokVO listaAlternativasNok;

    /**
     * Obtém o valor da propriedade codPerguntaNok.
     */
    public int getCodPerguntaNok() {
        return codPerguntaNok;
    }

    /**
     * Define o valor da propriedade codPerguntaNok.
     */
    public void setCodPerguntaNok(int value) {
        this.codPerguntaNok = value;
    }

    /**
     * Obtém o valor da propriedade descricaoPerguntaNok.
     *
     * @return possible object is {@link String }
     */
    public String getDescricaoPerguntaNok() {
        return descricaoPerguntaNok;
    }

    /**
     * Define o valor da propriedade descricaoPerguntaNok.
     *
     * @param value allowed object is {@link String }
     */
    public void setDescricaoPerguntaNok(String value) {
        this.descricaoPerguntaNok = value;
    }

    /**
     * Obtém o valor da propriedade listaAlternativasNok.
     *
     * @return possible object is {@link ArrayOfAlternativasNokVO }
     */
    public ArrayOfAlternativasNokVO getListaAlternativasNok() {
        return listaAlternativasNok;
    }

    /**
     * Define o valor da propriedade listaAlternativasNok.
     *
     * @param value allowed object is {@link ArrayOfAlternativasNokVO }
     */
    public void setListaAlternativasNok(ArrayOfAlternativasNokVO value) {
        this.listaAlternativasNok = value;
    }
}
