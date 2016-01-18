package br.com.zalf.prolog.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.services.TreinamentoService;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {
	private TreinamentoService service = new TreinamentoService();
	
	@POST
	public Response marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) {
		treinamentoColaborador.setDataVisualizacao(new Date(System.currentTimeMillis()));
		if (service.marcarTreinamentoComoVisto(treinamentoColaborador)) {
			return Response.Ok("Treinamento marcado com sucesso");
		} else {
			return Response.Error("Erro ao marcar treinamento");
		}
	}
	
	@POST
	@Path("/vistosColaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Treinamento> getVistosByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getVistosByColaborador(cpf, token);
	}
	
	@POST
	@Path("/naoVistosColaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Treinamento> getNaoVistosByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getNaoVistosByColaborador(cpf, token);
	}
}
