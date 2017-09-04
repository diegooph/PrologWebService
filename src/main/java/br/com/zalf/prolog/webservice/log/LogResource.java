package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/log")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogResource {

	private LogService service = new LogService();

	@POST
	@Secured
	@Path("/{identificador}")
	@Consumes(MediaType.TEXT_PLAIN + ";charset=utf-8")
	public Response insert(String log, @PathParam("identificador") String identificador) {
		if (service.insert(log, identificador.toUpperCase())) {
			return Response.ok("Log inserido com sucesso.");
		}else{
			return Response.error("Erro ao inserir o log.");
		}
		
	}

}
