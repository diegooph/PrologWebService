package br.com.zalf.prolog.webservice.errorhandling.exception;

import javax.ws.rs.core.Response;

/**
 * Created by Zart on 04/02/17.
 */
public class AmazonCredentialsException extends ProLogException {

    public AmazonCredentialsException() {
        super();
    }

    @Override
    public int getHttpStatusCode() {
        return Response.Status.NOT_FOUND.getStatusCode();
    }

    @Override
    public int getApplicationErrorCode() {
        return ProLogErrorCodes.AMAZON_CREDENTIALS.errorCode();
    }

    @Override
    public String getMessage() {
        return "Sem credencial cadastrada";
    }

    @Override
    public String getDeveloperMessage() {
        return "Tabela AMAZON_CREDENTIALS não possui dados";
    }

    @Override
    public String getMoreInfoLink() {
        return null;
    }
}