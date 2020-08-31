package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;

/**
 * Created on 2020-08-31
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AvaCorpAvilanConstants {
    private AvaCorpAvilanConstants() {
        throw new IllegalStateException(AvaCorpAvilanConstants.class.getSimpleName() + " cannot be instantiated!");
    }

    // Informações estáticas para criação do serviço de sincronia de dados da Avilan.
    public static final SistemaKey SISTEMA_KEY_AVILAN = SistemaKey.AVACORP_AVILAN;
    public static final MetodoIntegrado INSERT_OS = MetodoIntegrado.INSERT_OS;
    public static final Long CODIGO_EMPRESA_AVILAN = BuildConfig.DEBUG ? 3L : 2L;
    // Informações estáticas utilizadas para criar objetos da Avilan.
    public static final String COD_GRUPO_AVILAN = "1";
    public static final String COD_EMPRESA_AVILAN = "1";
    public static final String COD_USUARIO_AVILAN = "100";
    public static final String COD_TIPO_MANUTENCAO_AVILAN = "2";
    public static final String COD_OBJETIVO_ORDEM_SERVICO_AVILAN = "1";
}
