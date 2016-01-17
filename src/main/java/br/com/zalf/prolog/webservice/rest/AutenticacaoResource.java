package br.com.zalf.prolog.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.services.AutenticacaoService;

@Path("/autenticacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AutenticacaoResource {
	private AutenticacaoService service = new AutenticacaoService();
	
	@POST
	@Path("/verifyIfExists")
	public Response verifyIfExists(Autenticacao autenticacao) {		
		if (service.verifyIfExists(autenticacao)) {
			return Response.Ok("Token encontrado, usuário liberado para realizar requisições");
		} else {
			return Response.Error("Token não encontrado, usuário bloqueado para realizar requisições");
		}
	}
	
//	@POST
//	@Path("/insertOrUpdate")
//	public Autenticacao insertOrUpdate(Long cpf) {
//		return service.insertOrUpdate(cpf);
//	}

}
