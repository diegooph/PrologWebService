package br.com.empresa.oprojeto.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.AppVersion;
import br.com.empresa.oprojeto.models.Response;
import br.com.empresa.oprojeto.webservice.services.AppService;

@Path("/app")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AppResource {
	private AppService service = new AppService();
	
	@POST
	@Path("/version")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response isCurrentVersion(@FormParam("appVersion") AppVersion appVersion) {		
		if (service.isCurrentVersion(appVersion)) {
			return Response.Ok("Você está com a versão mais atualizada");
		} else {
			return Response.Error("Nova versão disponível para download");
		}
	}
}
