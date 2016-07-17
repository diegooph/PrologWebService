package br.com.zalf.prolog.webservice.errorhandling.exception;

public class Test1Exception extends ProLogException {

	@Override
	protected ApplicationExceptions whatIsYourType() {
		return ApplicationExceptions.TEST1;
	}
}