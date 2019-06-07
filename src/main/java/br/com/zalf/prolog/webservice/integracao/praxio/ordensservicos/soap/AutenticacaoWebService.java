
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>Classe Java de AutenticacaoWebService complex type.
 *
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 *
 * <pre>
 * &lt;complexType name="AutenticacaoWebService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ShortCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="NomeMetodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AutenticacaoWebService",
        propOrder = {"token",
                "shortCode",
                "nomeMetodo"})
public class AutenticacaoWebService {

    @XmlElement(name = "Token")
    protected String token;
    @XmlElement(name = "ShortCode")
    protected int shortCode;
    @XmlElement(name = "NomeMetodo")
    protected String nomeMetodo;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtém o valor da propriedade token.
     *
     * @return possible object is {@link String }
     */
    public String getToken() {
        return token;
    }

    /**
     * Define o valor da propriedade token.
     *
     * @param value allowed object is {@link String }
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Obtém o valor da propriedade shortCode.
     */
    public int getShortCode() {
        return shortCode;
    }

    /**
     * Define o valor da propriedade shortCode.
     */
    public void setShortCode(int value) {
        this.shortCode = value;
    }

    /**
     * Obtém o valor da propriedade nomeMetodo.
     *
     * @return possible object is {@link String }
     */
    public String getNomeMetodo() {
        return nomeMetodo;
    }

    /**
     * Define o valor da propriedade nomeMetodo.
     *
     * @param value allowed object is {@link String }
     */
    public void setNomeMetodo(String value) {
        this.nomeMetodo = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     * <p>
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }
}
