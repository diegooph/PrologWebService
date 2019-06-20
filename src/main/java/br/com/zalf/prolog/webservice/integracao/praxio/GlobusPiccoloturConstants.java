package br.com.zalf.prolog.webservice.integracao.praxio;

/**
 * Created on 08/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturConstants {

    private GlobusPiccoloturConstants() {
        throw new IllegalStateException(GlobusPiccoloturConstants.class.getSimpleName() + "can not be instantiated");
    }

    public static final String WSDL_LOCATION = "http://sp.bgmrodotec.com.br:8184/vanderlei.junior/ManutencaoWSTerceiros.asmx?wsdl";
    public static final String NAMESPACE = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros";
}
