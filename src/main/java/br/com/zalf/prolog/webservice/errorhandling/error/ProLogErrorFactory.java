package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.sun.istack.internal.NotNull;

public class ProLogErrorFactory {

	private ProLogErrorFactory() {
		throw new IllegalStateException(ProLogErrorFactory.class.getSimpleName() + " cannot be instantiated!");
	}

	@NotNull
	public static ProLogError create(ProLogException ex) {
		return ProLogError.createFrom(ex);
	}
}