package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ApplicationExceptions;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;

public class ErrorMessageFactory {

	private ErrorMessageFactory() {

	}

	public static ErrorMessage create(ProLogException ex) {
		ErrorMessage errorMessage = new ErrorMessage();
		ApplicationExceptions type = ex.getType();
		errorMessage.setDeveloperMessage(ex.getDeveloperMessage() == null ? type.developerMessage : ex.getDeveloperMessage());
		errorMessage.setMessage(ex.getMessage() == null ? type.message : ex.getMessage());
		errorMessage.setApplicationErrorCode(type.applicationErrorCode);	
		errorMessage.setHttpStatusCode(type.httpStatusCode);
		errorMessage.setMoreInfoLink(type.moreInfoLink);
		return errorMessage;
	}
}