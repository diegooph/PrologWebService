package br.com.zalf.prolog.webservice.errorhandling.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;

@Provider
public class ProLogExceptionMapper implements ExceptionMapper<ProLogException>{

	private static final String TAG = ProLogExceptionMapper.class.getSimpleName();

	public Response toResponse(ProLogException ex) {
		System.out.println("Teste: ");
		return Response
				.noContent()
				.entity(ErrorMessageFactory.create(ex))
				.type(MediaType.APPLICATION_JSON).
				build();
	}
	

}
