package br.com.zalf.prolog.webservice.log;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

@Path("/log")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogResource {

	LogService service = new LogService();

	@POST
	@Secured
	@Path("/{indicador}")
	@Consumes(MediaType.TEXT_PLAIN + ";charset=utf-8")
	public Response insert(String log, @PathParam("indicador") String indicador) {
		if (service.insert(log, indicador.toUpperCase())) {
			return Response.Ok("Log inserido com sucesso.");
		}else{
			return Response.Error("Erro ao inserir o log.");
		}
		
	}

}
