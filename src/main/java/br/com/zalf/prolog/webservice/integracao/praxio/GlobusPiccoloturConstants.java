package br.com.zalf.prolog.webservice.integracao.praxio;

/**
 * Created on 08/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturConstants {

    public static final String WSDL_LOCATION = "http://sp.bgmrodotec.com.br:8184/bruno.maia/ManutencaoWsTerceiros.asmx";
    public static final String NAMESPACE = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros";


    public static final String TOKEN = "MTc0Nzs0OTk7ODEzNA==";
    public static final int SHORT_CODE = 1032;
    public static final String METODO_PARA_LIBERAR = "GerarOrdemDeServicoCorretivaProlog";
    public static final String USUARIO_PROLOG_INTEGRACAO = "MANAGER";


    private GlobusPiccoloturConstants() {
        throw new IllegalStateException(GlobusPiccoloturConstants.class.getSimpleName() + "can not be instantiated");
    }
}
