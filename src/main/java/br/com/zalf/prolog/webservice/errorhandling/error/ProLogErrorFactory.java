package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

public final class ProLogErrorFactory {

	private ProLogErrorFactory() {
		throw new IllegalStateException(ProLogErrorFactory.class.getSimpleName() + " cannot be instantiated!");
	}

	@NotNull
	public static ProLogError create(@NotNull final ProLogException ex) {
		return ProLogError.createFrom(ex);
	}

	@NotNull
	public static ProLogError create(@NotNull final NotAuthorizedException ex) {
		return ProLogError.createFrom(ex);
	}

	@NotNull
	public static ProLogError create(@NotNull final ForbiddenException ex) {
		return ProLogError.createFrom(ex);
	}

	@NotNull
	public static ProLogError create(@NotNull final NotFoundException ex) {
		return ProLogError.createFrom(ex);
	}
}