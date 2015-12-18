package br.com.empresa.oprojeto.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.FaleConosco;
import br.com.empresa.oprojeto.models.Response;
import br.com.empresa.oprojeto.webservice.services.FaleConoscoService;

@Path("/faleConosco")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FaleConoscoResource {
	private FaleConoscoService service = new FaleConoscoService();
	
	// TODO: testar insert e update
	@POST
	public Response insert(FaleConosco faleConosco) {
		if (service.insert(faleConosco)) {
			return Response.Ok("Fale conosco inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir fale conosco");
		}
	}
	
	@PUT
	public Response update(FaleConosco faleConosco) {
		if (service.update(faleConosco)) {
			return Response.Ok("Fale conosco atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar fale conosco");
		}
	}
	
	@GET
	@Path("{codigo}")
	public FaleConosco getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}
	
	@GET
	public List<FaleConosco> getAll() {
		return service.getAll();
	}
	
	@GET
	@Path("/colaborador/{cpf}")
	public List<FaleConosco> getByColaborador(@PathParam("cpf") Long cpf) {
		return service.getByColaborador(cpf);
	}
	
	@DELETE
	@Path("{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if (service.delete(codigo)) {
			return Response.Ok("Fale conosco deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar fale conosco");
		}
	}
}
