package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created by luiz on 13/02/17.
 */
public class EnvironmentHelper {
    public static final String PROLOG_RDS_HOSTNAME;
    public static final String PROLOG_RDS_DB_NAME;
    public static final String PROLOG_RDS_USERNAME;
    public static final String PROLOG_RDS_PASSWORD;
    public static final String PROLOG_RDS_PORT;


    static {
        PROLOG_RDS_HOSTNAME = System.getenv("PROLOG_RDS_HOSTNAME");
        PROLOG_RDS_DB_NAME = System.getenv("PROLOG_RDS_DB_NAME");
        PROLOG_RDS_USERNAME = System.getenv("PROLOG_RDS_USERNAME");
        PROLOG_RDS_PASSWORD = System.getenv("PROLOG_RDS_PASSWORD");
        PROLOG_RDS_PORT = System.getenv("PROLOG_RDS_PORT");
    }
}