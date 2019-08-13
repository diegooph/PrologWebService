package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

public final class ProLogErrorFactory {

	private ProLogErrorFactory() {
		throw new IllegalStateException(ProLogErrorFactory.class.getSimpleName() + " cannot be instantiated!");
	}

	@NotNull
	public static ProLogError create(@NotNull final ProLogException ex) {
		return ProLogError.createFrom(ex);
	}
}