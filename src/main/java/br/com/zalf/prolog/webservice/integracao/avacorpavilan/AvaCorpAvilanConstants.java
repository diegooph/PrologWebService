package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;

/**
 * Created on 9/28/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@SuppressWarnings("ConstantConditions")
public final class AvaCorpAvilanConstants {

    public static final SistemaKey SISTEMA_KEY_AVILAN = SistemaKey.AVACORP_AVILAN;
    public static final Long CODIGO_EMPRESA_AVILAN = 2L;
    private static final String BASE_URL_TESTES = "http://prolog.avaconcloud.com/Avilan/IntegracaoPrologTestes/";
    private static final String BASE_URL_PROD = "http://prolog.avaconcloud.com/Avilan/IntegracaoProlog/";
    public static final String BASE_URL = BuildConfig.DEBUG ? BASE_URL_TESTES : BASE_URL_PROD;
    private static final String NAMESPACE_TESTES = "http://www.avacorp.com.br/integracaoprologtestes";
    private static final String NAMESPACE_PROD = "http://www.avacorp.com.br/integracaoprolog";
    public static final String NAMESPACE = BuildConfig.DEBUG ? NAMESPACE_TESTES : NAMESPACE_PROD;

    private AvaCorpAvilanConstants() {

    }

}