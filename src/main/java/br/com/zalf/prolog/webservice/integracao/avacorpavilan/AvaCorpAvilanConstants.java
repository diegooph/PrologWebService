package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.BuildConfig;

/**
 * Created on 9/28/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@SuppressWarnings("ConstantConditions")
public class AvaCorpAvilanConstants {

    private AvaCorpAvilanConstants() {

    }

    public static final String BASE_URL;
    public static final String NAMESPACE;

    static {
        final String BASE_URL_TESTES = "http://189.11.175.146/IntegracaoPrologTestes/";
        final String BASE_URL_PROD = "http://189.11.175.146/IntegracaoProlog/";
        BASE_URL = BuildConfig.DEBUG ? BASE_URL_PROD : BASE_URL_TESTES;

        final String NAMESPACE_TESTES = "http://www.avacorp.com.br/integracaoprologtestes";
        final String NAMESPACE_PROD = "http://www.avacorp.com.br/integracaoprolog";
        NAMESPACE = BuildConfig.DEBUG ? NAMESPACE_TESTES : NAMESPACE_PROD;
    }
}