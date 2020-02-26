
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Classe Java de OrdemDeServicoCorretivaPrologVO complex type.
 * <p>
 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * <pre>
 * &lt;complexType name="OrdemDeServicoCorretivaPrologVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodUnidadeChecklist" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CodChecklistRealizado" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CodModeloChecklist" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CpfColaboradorRealizacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PlacaVeiculoChecklist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KmColetadoChecklist" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TipoChecklist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataHoraRealizacaoUtc" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Usuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListaPerguntasNokVO" type="{http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros}ArrayOfPerguntasNokVO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "OrdemDeServicoCorretivaPrologVO",
        propOrder = {
                "codUnidadeChecklist",
                "codChecklistRealizado",
                "codModeloChecklist",
                "cpfColaboradorRealizacao",
                "placaVeiculoChecklist",
                "kmColetadoChecklist",
                "tipoChecklist",
                "dataHoraRealizacaoUtc",
                "usuario",
                "listaPerguntasNokVO"})
public class OrdemDeServicoCorretivaPrologVO {

    @XmlElement(name = "CodUnidadeChecklist")
    protected int codUnidadeChecklist;
    @XmlElement(name = "CodChecklistRealizado")
    protected int codChecklistRealizado;
    @XmlElement(name = "CodModeloChecklist")
    protected int codModeloChecklist;
    @XmlElement(name = "CpfColaboradorRealizacao")
    protected String cpfColaboradorRealizacao;
    @XmlElement(name = "PlacaVeiculoChecklist")
    protected String placaVeiculoChecklist;
    @XmlElement(name = "KmColetadoChecklist")
    protected int kmColetadoChecklist;
    @XmlElement(name = "TipoChecklist")
    protected String tipoChecklist;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataHoraRealizacaoUtc;
    @XmlElement(name = "Usuario")
    protected String usuario;
    @XmlElement(name = "ListaPerguntasNokVO")
    protected ArrayOfPerguntasNokVO listaPerguntasNokVO;

    /**
     * Obtém o valor da propriedade codUnidadeChecklist.
     */
    public int getCodUnidadeChecklist() {
        return codUnidadeChecklist;
    }

    /**
     * Define o valor da propriedade codUnidadeChecklist.
     */
    public void setCodUnidadeChecklist(int value) {
        this.codUnidadeChecklist = value;
    }

    /**
     * Obtém o valor da propriedade codChecklistRealizado.
     */
    public int getCodChecklistRealizado() {
        return codChecklistRealizado;
    }

    /**
     * Define o valor da propriedade codChecklistRealizado.
     */
    public void setCodChecklistRealizado(int value) {
        this.codChecklistRealizado = value;
    }

    /**
     * Obtém o valor da propriedade codModeloChecklist.
     */
    public int getCodModeloChecklist() {
        return codModeloChecklist;
    }

    /**
     * Define o valor da propriedade codModeloChecklist.
     */
    public void setCodModeloChecklist(final int codModeloChecklist) {
        this.codModeloChecklist = codModeloChecklist;
    }

    /**
     * Obtém o valor da propriedade cpfColaboradorRealizacao.
     *
     * @return possible object is {@link String }
     */
    public String getCpfColaboradorRealizacao() {
        return cpfColaboradorRealizacao;
    }

    /**
     * Define o valor da propriedade cpfColaboradorRealizacao.
     *
     * @param value allowed object is {@link String }
     */
    public void setCpfColaboradorRealizacao(String value) {
        this.cpfColaboradorRealizacao = value;
    }

    /**
     * Obtém o valor da propriedade placaVeiculoChecklist.
     *
     * @return possible object is {@link String }
     */
    public String getPlacaVeiculoChecklist() {
        return placaVeiculoChecklist;
    }

    /**
     * Define o valor da propriedade placaVeiculoChecklist.
     *
     * @param value allowed object is {@link String }
     */
    public void setPlacaVeiculoChecklist(String value) {
        this.placaVeiculoChecklist = value;
    }

    /**
     * Obtém o valor da propriedade kmColetadoChecklist.
     */
    public int getKmColetadoChecklist() {
        return kmColetadoChecklist;
    }

    /**
     * Define o valor da propriedade kmColetadoChecklist.
     */
    public void setKmColetadoChecklist(int value) {
        this.kmColetadoChecklist = value;
    }

    /**
     * Obtém o valor da propriedade tipoChecklist.
     *
     * @return possible object is {@link String }
     */
    public String getTipoChecklist() {
        return tipoChecklist;
    }

    /**
     * Define o valor da propriedade tipoChecklist.
     *
     * @param value allowed object is {@link String }
     */
    public void setTipoChecklist(String value) {
        this.tipoChecklist = value;
    }

    /**
     * Obtém o valor da propriedade dataHoraRealizacaoUtc.
     *
     * @return possible object is {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getDataHoraRealizacaoUtc() {
        return dataHoraRealizacaoUtc;
    }

    /**
     * Define o valor da propriedade dataHoraRealizacaoUtc.
     *
     * @param value allowed object is {@link XMLGregorianCalendar }
     */
    public void setDataHoraRealizacaoUtc(XMLGregorianCalendar value) {
        this.dataHoraRealizacaoUtc = value;
    }

    /**
     * Obtém o valor da propriedade usuario.
     *
     * @return possible object is {@link String }
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     *
     * @param value allowed object is {@link String }
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade listaPerguntasNokVO.
     *
     * @return possible object is {@link ArrayOfPerguntasNokVO }
     */
    public ArrayOfPerguntasNokVO getListaPerguntasNokVO() {
        return listaPerguntasNokVO;
    }

    /**
     * Define o valor da propriedade listaPerguntasNokVO.
     *
     * @param value allowed object is {@link ArrayOfPerguntasNokVO }
     */
    public void setListaPerguntasNokVO(ArrayOfPerguntasNokVO value) {
        this.listaPerguntasNokVO = value;
    }
}
