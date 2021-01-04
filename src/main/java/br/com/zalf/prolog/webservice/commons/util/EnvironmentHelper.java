package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created on 13/02/2017
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class EnvironmentHelper {

    public static final String PROLOG_RDS_HOSTNAME;
    public static final String PROLOG_RDS_DB_NAME;
    public static final String PROLOG_RDS_USERNAME;
    public static final String PROLOG_RDS_PASSWORD;
    public static final String PROLOG_RDS_PORT;
    public static final String SENTRY_DSN;
    public static final String GOOGLE_APPLICATION_CREDENTIALS;
    public static final String MAILJET_APIKEY_PUBLIC;
    public static final String MAILJET_APIKEY_PRIVATE;

    static {
        PROLOG_RDS_HOSTNAME = System.getenv("PROLOG_RDS_HOSTNAME");
        PROLOG_RDS_DB_NAME = System.getenv("PROLOG_RDS_DB_NAME");
        PROLOG_RDS_USERNAME = System.getenv("PROLOG_RDS_USERNAME");
        PROLOG_RDS_PASSWORD = System.getenv("PROLOG_RDS_PASSWORD");
        PROLOG_RDS_PORT = System.getenv("PROLOG_RDS_PORT");
        SENTRY_DSN = System.getenv("SENTRY_DSN");
        GOOGLE_APPLICATION_CREDENTIALS = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        MAILJET_APIKEY_PUBLIC = System.getenv("MAILJET_APIKEY_PUBLIC");
        MAILJET_APIKEY_PRIVATE = System.getenv("MAILJET_APIKEY_PRIVATE");
    }

    private EnvironmentHelper() {
        throw new IllegalStateException(EnvironmentHelper.class.getSimpleName() + " cannot be instantiated!");
    }
}