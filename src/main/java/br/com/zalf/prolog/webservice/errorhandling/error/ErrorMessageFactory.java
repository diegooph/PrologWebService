package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.sun.istack.internal.NotNull;

public class ErrorMessageFactory {

	private ErrorMessageFactory() {
		throw new IllegalStateException(ErrorMessageFactory.class.getSimpleName() + " cannot be instantiated!");
	}

	@NotNull
	public static ErrorMessage create(ProLogException ex) {
		final ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setDeveloperMessage(ex.getDeveloperMessage());
		errorMessage.setMessage(ex.getMessage());
		errorMessage.setApplicationErrorCode(ex.getApplicationErrorCode());
		errorMessage.setHttpStatusCode(ex.getHttpStatusCode());
		errorMessage.setMoreInfoLink(ex.getMoreInfoLink());
		return errorMessage;
	}
}