package br.com.empresa.oprojeto.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.treinamento.Treinamento;
import br.com.empresa.oprojeto.webservice.services.TreinamentoService;

@Path("/treinamentos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class TreinamentoResource {
	private TreinamentoService service = new TreinamentoService();
	
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
