package br.com.zalf.prolog.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.AppVersion;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.services.AppService;

@Path("/app")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AppResource {
	private AppService service = new AppService();
	
	@POST
	@Path("/version")
	public Response isThisCurrentVersion(AppVersion appVersion) {		
		if (service.isThisCurrentVersion(appVersion)) {
			return Response.Ok("Você está com a versão mais atualizada");
		} else {
			return Response.Error("Nova versão disponível para download");
		}
	}
}
