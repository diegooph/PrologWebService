
package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Veiculo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="Veiculo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="placa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoVeiculo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="marcador" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Veiculo", propOrder = {
    "placa",
    "tipoVeiculo",
    "marcador"
})
public class Veiculo {

    protected String placa;
    protected int tipoVeiculo;
    protected int marcador;

    /**
     * Obtém o valor da propriedade placa.
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
     * Define o valor da propriedade placa.
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
     * Obtém o valor da propriedade tipoVeiculo.
     * 
     */
    public int getTipoVeiculo() {
        return tipoVeiculo;
    }

    /**
     * Define o valor da propriedade tipoVeiculo.
     * 
     */
    public void setTipoVeiculo(int value) {
        this.tipoVeiculo = value;
    }

    /**
     * Obtém o valor da propriedade marcador.
     * 
     */
    public int getMarcador() {
        return marcador;
    }

    /**
     * Define o valor da propriedade marcador.
     * 
     */
    public void setMarcador(int value) {
        this.marcador = value;
    }

}
