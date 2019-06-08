
package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AutenticacaoWebService_QNAME =
            new QName(
                    "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros",
                    "AutenticacaoWebService");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EnviaServicosExecutadosPrologResponse }
     * 
     */
    public EnviaServicosExecutadosPrologResponse createEnviaServicosExecutadosPrologResponse() {
        return new EnviaServicosExecutadosPrologResponse();
    }

    /**
     * Create an instance of {@link RetornoEnvioPrologVO }
     * 
     */
    public RetornoEnvioPrologVO createRetornoEnvioPrologVO() {
        return new RetornoEnvioPrologVO();
    }

    /**
     * Create an instance of {@link EnviaOsAbertaProlog }
     * 
     */
    public EnviaOsAbertaProlog createEnviaOsAbertaProlog() {
        return new EnviaOsAbertaProlog();
    }

    /**
     * Create an instance of {@link GerarOrdemDeServicoCorretiva }
     * 
     */
    public GerarOrdemDeServicoCorretiva createGerarOrdemDeServicoCorretiva() {
        return new GerarOrdemDeServicoCorretiva();
    }

    /**
     * Create an instance of {@link OrdemDeServicoCorretivaVO }
     * 
     */
    public OrdemDeServicoCorretivaVO createOrdemDeServicoCorretivaVO() {
        return new OrdemDeServicoCorretivaVO();
    }

    /**
     * Create an instance of {@link AutenticacaoWebService }
     * 
     */
    public AutenticacaoWebService createAutenticacaoWebService() {
        return new AutenticacaoWebService();
    }

    /**
     * Create an instance of {@link EnviaServicosExecutadosProlog }
     * 
     */
    public EnviaServicosExecutadosProlog createEnviaServicosExecutadosProlog() {
        return new EnviaServicosExecutadosProlog();
    }

    /**
     * Create an instance of {@link EnviaOsAbertaPrologResponse }
     * 
     */
    public EnviaOsAbertaPrologResponse createEnviaOsAbertaPrologResponse() {
        return new EnviaOsAbertaPrologResponse();
    }

    /**
     * Create an instance of {@link GerarOrdemDeServicoCorretivaResponse }
     * 
     */
    public GerarOrdemDeServicoCorretivaResponse createGerarOrdemDeServicoCorretivaResponse() {
        return new GerarOrdemDeServicoCorretivaResponse();
    }

    /**
     * Create an instance of {@link RetornoOsCorretivaVO }
     * 
     */
    public RetornoOsCorretivaVO createRetornoOsCorretivaVO() {
        return new RetornoOsCorretivaVO();
    }

    /**
     * Create an instance of {@link GerarOrdemDeServicoCorretivaProlog }
     * 
     */
    public GerarOrdemDeServicoCorretivaProlog createGerarOrdemDeServicoCorretivaProlog() {
        return new GerarOrdemDeServicoCorretivaProlog();
    }

    /**
     * Create an instance of {@link OrdemDeServicoCorretivaPrologVO }
     * 
     */
    public OrdemDeServicoCorretivaPrologVO createOrdemDeServicoCorretivaPrologVO() {
        return new OrdemDeServicoCorretivaPrologVO();
    }

    /**
     * Create an instance of {@link GerarOrdemDeServicoCorretivaPrologResponse }
     * 
     */
    public GerarOrdemDeServicoCorretivaPrologResponse createGerarOrdemDeServicoCorretivaPrologResponse() {
        return new GerarOrdemDeServicoCorretivaPrologResponse();
    }

    /**
     * Create an instance of {@link AlternativasNokVO }
     * 
     */
    public AlternativasNokVO createAlternativasNokVO() {
        return new AlternativasNokVO();
    }

    /**
     * Create an instance of {@link ArrayOfPerguntasNokVO }
     * 
     */
    public ArrayOfPerguntasNokVO createArrayOfPerguntasNokVO() {
        return new ArrayOfPerguntasNokVO();
    }

    /**
     * Create an instance of {@link ArrayOfAlternativasNokVO }
     * 
     */
    public ArrayOfAlternativasNokVO createArrayOfAlternativasNokVO() {
        return new ArrayOfAlternativasNokVO();
    }

    /**
     * Create an instance of {@link ArrayOfGrupoDefeitoVO }
     * 
     */
    public ArrayOfGrupoDefeitoVO createArrayOfGrupoDefeitoVO() {
        return new ArrayOfGrupoDefeitoVO();
    }

    /**
     * Create an instance of {@link PerguntasNokVO }
     * 
     */
    public PerguntasNokVO createPerguntasNokVO() {
        return new PerguntasNokVO();
    }

    /**
     * Create an instance of {@link GrupoDefeitoVO }
     * 
     */
    public GrupoDefeitoVO createGrupoDefeitoVO() {
        return new GrupoDefeitoVO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AutenticacaoWebService }{@code >}}
     * 
     */
    @XmlElementDecl(
            namespace = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros",
            name = "AutenticacaoWebService")
    public JAXBElement<AutenticacaoWebService> createAutenticacaoWebService(AutenticacaoWebService value) {
        return new JAXBElement<>(_AutenticacaoWebService_QNAME, AutenticacaoWebService.class, null, value);
    }

}
