package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.sun.istack.internal.NotNull;

public class ProLogErrorFactory {

	private ProLogErrorFactory() {
		throw new IllegalStateException(ProLogErrorFactory.class.getSimpleName() + " cannot be instantiated!");
	}

	@NotNull
	public static ProLogError create(ProLogException ex) {
		final ProLogError proLogError = new ProLogError();
		proLogError.setDeveloperMessage(ex.getDeveloperMessage());
		proLogError.setMessage(ex.getMessage());
		proLogError.setApplicationErrorCode(ex.getApplicationErrorCode());
		proLogError.setHttpStatusCode(ex.getHttpStatusCode());
		proLogError.setMoreInfoLink(ex.getMoreInfoLink());
		return proLogError;
	}
}