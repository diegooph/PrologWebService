package br.com.zalf.prolog.webservice.errorhandling.exception;

/**
 * Created by Zart on 04/02/17.
 */
public class AmazonCredentialsException extends ProLogException {

    public AmazonCredentialsException(int statusCode, int proLogErrorCode, String message, String developerMessage) {
        super(statusCode, proLogErrorCode, message, developerMessage);
    }
}