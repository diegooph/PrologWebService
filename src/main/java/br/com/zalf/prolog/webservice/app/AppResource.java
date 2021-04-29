package br.com.zalf.prolog.webservice.app;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v2/app")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AppResource {
	private final AppService service = new AppService();

	@POST
	@Path("/version")
	@Secured
	public Response isThisCurrentVersion(AppVersion appVersion) {
		if (service.isThisCurrentVersion(appVersion)) {
			return Response.ok("Você está com a versão mais atualizada");
		} else {
			return Response.error("Nova versão disponível para download");
		}
	}
}