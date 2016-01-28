package br.com.zalf.prolog.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.SolicitacaoFolga;
import br.com.zalf.prolog.webservice.services.SolicitacaoFolgaService;

@Path("/solicitacaoFolga")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SolicitacaoFolgaResource {
	private SolicitacaoFolgaService service = new SolicitacaoFolgaService();
	
	@POST
	public Response insert(SolicitacaoFolga solicitacaoFolga) {
		if (service.insert(solicitacaoFolga)) {
			return Response.Ok("Solicitação de folga inserida com sucesso");
		} else {
			return Response.Error("Erro ao inserir solicitação de folga");
		}
	}
	
	@POST
	@Path("/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getByColaborador(cpf, token);
	}
	
}
