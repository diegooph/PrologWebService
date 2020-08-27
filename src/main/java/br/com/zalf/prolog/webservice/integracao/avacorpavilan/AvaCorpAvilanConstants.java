package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;

/**
 * Created on 9/28/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AvaCorpAvilanConstants {
    private static final String BASE_URL_TESTES = "http://prolog.avaconcloud.com/Avilan/IntegracaoPrologTestes/";
    private static final String BASE_URL_PROD = "http://prolog.avaconcloud.com/Avilan/IntegracaoProlog/";
    public static final String BASE_URL = BuildConfig.DEBUG ? BASE_URL_TESTES : BASE_URL_PROD;
    private static final String NAMESPACE_TESTES = "http://www.avacorp.com.br/integracaoprologtestes";
    private static final String NAMESPACE_PROD = "http://www.avacorp.com.br/integracaoprolog";
    public static final String NAMESPACE = BuildConfig.DEBUG ? NAMESPACE_TESTES : NAMESPACE_PROD;

    // Informações estáticas para criação do serviço de sincronia de dados da Avilan.
    public static final SistemaKey SISTEMA_KEY_AVILAN = SistemaKey.AVACORP_AVILAN;
    public static final Long CODIGO_EMPRESA_AVILAN = BuildConfig.DEBUG ? 3L : 2L;
    // Informações estáticas utilizadas para criar objetos da Avilan.
    public static final String COD_GRUPO_AVILAN = "1";
    public static final String COD_EMPRESA_AVILAN = "1";
    public static final String COD_USUARIO_AVILAN = "100";
    public static final String COD_TIPO_MANUTENCAO_AVILAN = "2";
    public static final String COD_OBJETIVO_ORDEM_SERVICO_AVILAN = "1";

    private AvaCorpAvilanConstants() {
    }
}