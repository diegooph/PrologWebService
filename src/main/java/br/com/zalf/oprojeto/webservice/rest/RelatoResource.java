package br.com.zalf.oprojeto.webservice.rest;

import java.util.Date;
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

import br.com.empresa.oprojeto.models.Relato;
import br.com.empresa.oprojeto.models.Response;
import br.com.zalf.oprojeto.webservice.services.RelatoService;

@Path("/relatos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatoResource {
	private RelatoService service = new RelatoService();
	
	@POST
	public Response insert(Relato relato) {
		relato.setDataDatabase(new Date(System.currentTimeMillis()));
		if (service.insert(relato)) {
			return Response.Ok("Relato inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir relato");
		}
	}
	
	@PUT
	public Response update(Relato relato) {
		if (service.update(relato)) {
			return Response.Ok("Relato atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o relato");
		}
	}
	
	@GET
	public List<Relato> getAll() {
		return service.getAll();
	}
	
	@GET
	@Path("{codigo}")
	public Relato getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}
	
	@GET
	@Path("/colaborador/{cpf}")
	public List<Relato> getByColaborador(@PathParam("cpf") Long cpf) {
		return service.getByColaborador(cpf);
	}
	
	@GET
	@Path("/exceto/colaborador/{cpf}")
	public List<Relato> getAllExcetoColaborador(@PathParam("cpf") Long cpf) {
		return service.getAllExcetoColaborador(cpf);
	}
	
	@DELETE
	@Path("{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if (service.delete(codigo)) {
			return Response.Ok("Relato deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar relato");
		}
	}
}
