package br.com.zalf.prolog.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.Response;
import br.com.empresa.oprojeto.models.treinamento.Treinamento;
import br.com.empresa.oprojeto.models.treinamento.TreinamentoColaborador;
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
	
	@GET
	@Path("/vistosColaborador/{cpf}")
	public List<Treinamento> getVistosByColaborador(@PathParam("cpf") Long cpf) {
		return service.getVistosByColaborador(cpf);
	}
	
	@GET
	@Path("/naoVistosColaborador/{cpf}")
	public List<Treinamento> getNaoVistosByColaborador(@PathParam("cpf") Long cpf) {
		return service.getNaoVistosByColaborador(cpf);
	}
}
