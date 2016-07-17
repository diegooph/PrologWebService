package br.com.zalf.prolog.webservice.errorhandling.exception;

public class Test2Exception extends ProLogException {

	@Override
	protected ApplicationExceptions whatIsYourType() {
		return ApplicationExceptions.TEST2;
	}
}