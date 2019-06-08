
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de OrdemDeServicoCorretivaVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="OrdemDeServicoCorretivaVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodigoEmpresa" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CodigoFilial" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CodigoGaragem" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PrefixoVeiculo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Usuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CodigoOrigemOS" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ListaGrupoDefeitos" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}ArrayOfGrupoDefeitoVO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrdemDeServicoCorretivaVO", propOrder = {
    "codigoEmpresa",
    "codigoFilial",
    "codigoGaragem",
    "prefixoVeiculo",
    "usuario",
    "codigoOrigemOS",
    "listaGrupoDefeitos"
})
public class OrdemDeServicoCorretivaVO {

    @XmlElement(name = "CodigoEmpresa")
    protected int codigoEmpresa;
    @XmlElement(name = "CodigoFilial")
    protected int codigoFilial;
    @XmlElement(name = "CodigoGaragem")
    protected int codigoGaragem;
    @XmlElement(name = "PrefixoVeiculo")
    protected String prefixoVeiculo;
    @XmlElement(name = "Usuario")
    protected String usuario;
    @XmlElement(name = "CodigoOrigemOS")
    protected int codigoOrigemOS;
    @XmlElement(name = "ListaGrupoDefeitos")
    protected ArrayOfGrupoDefeitoVO listaGrupoDefeitos;

    /**
     * Obtém o valor da propriedade codigoEmpresa.
     * 
     */
    public int getCodigoEmpresa() {
        return codigoEmpresa;
    }

    /**
     * Define o valor da propriedade codigoEmpresa.
     * 
     */
    public void setCodigoEmpresa(int value) {
        this.codigoEmpresa = value;
    }

    /**
     * Obtém o valor da propriedade codigoFilial.
     * 
     */
    public int getCodigoFilial() {
        return codigoFilial;
    }

    /**
     * Define o valor da propriedade codigoFilial.
     * 
     */
    public void setCodigoFilial(int value) {
        this.codigoFilial = value;
    }

    /**
     * Obtém o valor da propriedade codigoGaragem.
     * 
     */
    public int getCodigoGaragem() {
        return codigoGaragem;
    }

    /**
     * Define o valor da propriedade codigoGaragem.
     * 
     */
    public void setCodigoGaragem(int value) {
        this.codigoGaragem = value;
    }

    /**
     * Obtém o valor da propriedade prefixoVeiculo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrefixoVeiculo() {
        return prefixoVeiculo;
    }

    /**
     * Define o valor da propriedade prefixoVeiculo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrefixoVeiculo(String value) {
        this.prefixoVeiculo = value;
    }

    /**
     * Obtém o valor da propriedade usuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade codigoOrigemOS.
     * 
     */
    public int getCodigoOrigemOS() {
        return codigoOrigemOS;
    }

    /**
     * Define o valor da propriedade codigoOrigemOS.
     * 
     */
    public void setCodigoOrigemOS(int value) {
        this.codigoOrigemOS = value;
    }

    /**
     * Obtém o valor da propriedade listaGrupoDefeitos.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfGrupoDefeitoVO }
     *     
     */
    public ArrayOfGrupoDefeitoVO getListaGrupoDefeitos() {
        return listaGrupoDefeitos;
    }

    /**
     * Define o valor da propriedade listaGrupoDefeitos.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfGrupoDefeitoVO }
     *     
     */
    public void setListaGrupoDefeitos(ArrayOfGrupoDefeitoVO value) {
        this.listaGrupoDefeitos = value;
    }

}
