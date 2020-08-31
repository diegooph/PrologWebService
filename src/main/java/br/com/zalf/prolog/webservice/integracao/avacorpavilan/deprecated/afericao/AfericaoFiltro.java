
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.TipoVeiculoAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.ColaboradorAvilan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AfericaoFiltro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AfericaoFiltro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoAfericao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dataRealizacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="odometro" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pneus" type="{http://www.avacorp.com.br/integracaoprologtestes}ArrayOfPneuFiltro" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AfericaoFiltro", propOrder = {
    "codigoAfericao",
    "dataRealizacao",
    "placa",
    "odometro",
    "tipoVeiculo",
    "pneus",
    "colaborador"
})
public class AfericaoFiltro {

    protected int codigoAfericao;
    protected String dataRealizacao;
    protected String placa;
    protected int odometro;
    protected TipoVeiculoAvilan tipoVeiculo;
    protected ArrayOfPneuFiltro pneus;
    protected ColaboradorAvilan colaborador;

    /**
     * Gets the value of the codigoAfericao property.
     * 
     */
    public int getCodigoAfericao() {
        return codigoAfericao;
    }

    /**
     * Sets the value of the codigoAfericao property.
     * 
     */
    public void setCodigoAfericao(int value) {
        this.codigoAfericao = value;
    }

    /**
     * Gets the value of the dataRealizacao property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataRealizacao() {
        return dataRealizacao;
    }

    /**
     * Sets the value of the dataRealizacao property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataRealizacao(String value) {
        this.dataRealizacao = value;
    }

    /**
     * Gets the value of the placa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Sets the value of the placa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaca(String value) {
        this.placa = value;
    }

    /**
     * Gets the value of the odometro property.
     * 
     */
    public int getOdometro() {
        return odometro;
    }

    /**
     * Sets the value of the odometro property.
     * 
     */
    public void setOdometro(int value) {
        this.odometro = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link TipoVeiculoAvilan }
     *     
     */
    public TipoVeiculoAvilan getTipo() {
        return tipoVeiculo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoVeiculoAvilan }
     *     
     */
    public void setTipo(TipoVeiculoAvilan value) {
        this.tipoVeiculo = value;
    }

    /**
     * Gets the value of the pneus property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPneuFiltro }
     *     
     */
    public ArrayOfPneuFiltro getPneus() {
        return pneus;
    }

    /**
     * Sets the value of the pneus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPneuFiltro }
     *     
     */
    public void setPneus(ArrayOfPneuFiltro value) {
        this.pneus = value;
    }

    public ColaboradorAvilan getColaborador() {
        return colaborador;
    }

    public void setColaborador(ColaboradorAvilan colaborador) {
        this.colaborador = colaborador;
    }
}
