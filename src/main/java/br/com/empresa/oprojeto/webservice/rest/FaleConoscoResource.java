package br.com.empresa.oprojeto.webservice.rest;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import br.com.empresa.oprojeto.models.FaleConosco;
import br.com.empresa.oprojeto.models.Response;
import br.com.empresa.oprojeto.webservice.services.FaleConoscoService;

@Path("/faleConosco")
public class FaleConoscoResource {
	private FaleConoscoService service = new FaleConoscoService();
	
	// TODO: testar insert e update
	public Response insert(FaleConosco faleConosco) {
		if (service.insert(faleConosco)) {
			return Response.Ok("Fale conosco inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir fale conosco");
		}
	}
	
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
	@Path("/byColaborador/{cpf}")
	public List<FaleConosco> getByColaborador(@PathParam("cpf") Long cpf) {
		return service.getByColaborador(cpf);
	}
	
	@DELETE
	@Path("{cpf}")
	public Response delete(@PathParam("cpf") Long codigo) {
		if (service.delete(codigo)) {
			return Response.Ok("Fale conosco deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar fale conosco");
		}
	}
}
