package br.com.zalf.prolog.webservice.errorhandling.exception;

/**
 * Created by Zart on 04/02/17.
 */
public class AmazonCredentialsException extends ProLogException {


    public AmazonCredentialsException(String message, String developerMessage) {
        super(message, developerMessage);
    }

    @Override
    protected ApplicationExceptions whatIsYourType() {
        return null;
    }
}
