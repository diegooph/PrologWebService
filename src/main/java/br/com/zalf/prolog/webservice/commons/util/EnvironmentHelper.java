package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created on 13/02/2017
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class EnvironmentHelper {
    public static final String MAILJET_APIKEY_PUBLIC;
    public static final String MAILJET_APIKEY_PRIVATE;

    static {
        MAILJET_APIKEY_PUBLIC = System.getenv("MAILJET_APIKEY_PUBLIC");
        MAILJET_APIKEY_PRIVATE = System.getenv("MAILJET_APIKEY_PRIVATE");
    }

    private EnvironmentHelper() {
        throw new IllegalStateException(EnvironmentHelper.class.getSimpleName() + " cannot be instantiated!");
    }
}