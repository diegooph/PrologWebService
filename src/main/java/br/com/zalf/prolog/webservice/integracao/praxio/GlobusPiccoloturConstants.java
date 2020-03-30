package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 08/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturConstants {
    // #################################################################################################################
    // #################################################################################################################
    // #######           CONSTANTES UTILIZADAS PARA A FUNCIONDALIDADE DE CHECKLIST/ORDENS DE SERVIÇOS            #######
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    public static final String WSDL_LOCATION = BuildConfig.DEBUG
            ? "http://sp.bgmrodotec.com.br:8184/bruno.maia/ManutencaoWsTerceiros.asmx"
            : "http://erp.piccolotur.com.br:55582/GlobusMais/ManutencaoWsTerceiros.asmx";
    public static final String NAMESPACE = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros";

    public static final String TOKEN_AUTENTICACAO_OS =
            BuildConfig.DEBUG ? "MTc0Nzs0OTk7ODEzNA==" : "MTEzNzI7NDk5OzgxMzQ=";
    public static final int SHORT_CODE_AUTENTICACAO_OS = 1032;
    public static final String METODO_PARA_LIBERAR = "GerarOrdemDeServicoCorretivaProlog";
    public static final String USUARIO_PROLOG_INTEGRACAO = "MANAGER";

    private GlobusPiccoloturConstants() {
        throw new IllegalStateException(GlobusPiccoloturConstants.class.getSimpleName() + " can not be instantiated");
    }
}
