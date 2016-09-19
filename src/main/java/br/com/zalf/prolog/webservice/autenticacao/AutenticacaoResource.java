package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.commons.network.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/autenticacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AutenticacaoResource {

	private AutenticacaoService service = new AutenticacaoService();

	@DELETE
	@Path("{token}")
	public Response delete(@PathParam("token") String token) {
		if (service.delete(token)) {
			return Response.Ok("Token deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar token");
		}
	}
}
