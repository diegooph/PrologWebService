package br.com.zalf.prolog.webservice.errorhandling.error;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProLogExceptionMapper implements ExceptionMapper<ProLogException>{

	public Response toResponse(ProLogException ex) {
		return Response
				.noContent()
				.entity(ErrorMessageFactory.create(ex))
				.type(MediaType.APPLICATION_JSON).
				build();
	}
}