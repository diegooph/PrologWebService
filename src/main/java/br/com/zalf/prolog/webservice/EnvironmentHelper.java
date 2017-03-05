package br.com.zalf.prolog.webservice;

/**
 * Created by luiz on 13/02/17.
 */
public class EnvironmentHelper {
    static final String PROLOG_RDS_HOSTNAME;
    static final String PROLOG_RDS_DB_NAME;
    static final String PROLOG_RDS_USERNAME;
    static final String PROLOG_RDS_PASSWORD;
    static final String PROLOG_RDS_PORT;


    static {
        PROLOG_RDS_HOSTNAME = System.getenv("PROLOG_RDS_HOSTNAME");
        PROLOG_RDS_DB_NAME = System.getenv("PROLOG_RDS_DB_NAME");
        PROLOG_RDS_USERNAME = System.getenv("PROLOG_RDS_USERNAME");
        PROLOG_RDS_PASSWORD = System.getenv("PROLOG_RDS_PASSWORD");
        PROLOG_RDS_PORT = System.getenv("PROLOG_RDS_PORT");
    }
}