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
    // #######                       CONSTANTES UTILIZADAS PARA A INTEGRAÇÃO COM O GLOBUS                        #######
    // #################################################################################################################
    // #################################################################################################################
    @NotNull
    public static final String WSDL_LOCATION =
            "http://erp.piccolotur.com.br:55582/GlobusMais/ManutencaoWsTerceiros.asmx";
    public static final String NAMESPACE = "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros";
    public static final String TOKEN_AUTENTICACAO_OS =
            BuildConfig.DEBUG ? "MTc0Nzs0OTk7ODEzNA==" : "MTEzNzI7NDk5OzgxMzQ=";
    public static final int SHORT_CODE_AUTENTICACAO_OS = 1032;
    public static final String METODO_PARA_LIBERAR = "GerarOrdemDeServicoCorretivaProlog";
    public static final String METODO_ENVIO_OS_SOAP_ACTION =
            "http://bgmrodotec.com.br/globus5/ManutencaoWsTerceiros/GerarOrdemDeServicoCorretivaProlog";

    public static final String USUARIO_PROLOG_INTEGRACAO = "MANAGER";
    public static final String CPF_COLABORADOR_LOCAIS_MOVIMENTO = "35118345898";
    public static final String COD_UNIDADE_NOME_LOCAL_MOVIMENTO_SEPARATOR = " - ";
    /**
     * Utilizamos a <code>URL_TESTES</code> em ambiente de testes para evitar com o ambiente de testes da Praxio em
     * qualquer situação. Para eventos específicos onde queremos testar no ambiente da Praxio, modificaremos essa
     * validação.
     */
    @NotNull
    // URL_TESTES_LOCAL = "http://localhost:8184/bruno.maia/ManutencaoWsTerceiros.asmx"
    private static final String URL_TESTES = "http://sp.bgmrodotec.com.br:8184/bruno.maia/ManutencaoWsTerceiros.asmx";

    private GlobusPiccoloturConstants() {
        throw new IllegalStateException(GlobusPiccoloturConstants.class.getSimpleName() + " can not be instantiated");
    }
}
