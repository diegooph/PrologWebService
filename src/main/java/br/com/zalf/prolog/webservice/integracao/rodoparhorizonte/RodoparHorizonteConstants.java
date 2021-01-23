package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RodoparHorizonteConstants {
    // CONTANTES PARA PARÂMETROS DE REQUISIÇÂO COM O RODOPAR
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_GRANT_TYPE = "grant_type";
    public static final String HEADER_AUTHORIZATION = "authorization";
    // CONSTANTE PARA GERAÇÃO DO TOKEN
    // O espaço é necessário para a criação correta do token.
    public static final String BEARER_TOKEN = "Bearer ";

    private RodoparHorizonteConstants() {
        throw new IllegalStateException(RodoparHorizonteConstants.class.getSimpleName() + " cannot be instantiated!");
    }
}
