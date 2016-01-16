package br.com.zalf.prolog.webservice.rest;

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

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.webservice.services.ChecklistService;

@Path("/checklists")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource {
	private ChecklistService service = new ChecklistService();
	
	@POST
	public Response insert(Checklist checklist) {
		checklist.setData(new Date(System.currentTimeMillis()));
		if (service.insert(checklist)) {
			return Response.Ok("Checklist inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir checklist");
		}
	}
	
	@PUT
	public Response update(Checklist checklist) {
		if (service.update(checklist)) {
			return Response.Ok("Checklist atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o checklist");
		}
	}
	
	@GET
	@Path("{codigo}")
	public Checklist getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}
	
	@GET
	public List<Checklist> getAll() {
		return service.getAll();
	}
	
	@GET
	@Path("/exceto/colaborador/{cpf}")
	public List<Checklist> getAllExcetoColaborador(Long cpf) {
		return service.getAllExcetoColaborador(cpf);
	}
	
	@GET
	@Path("/colaborador/{cpf}")
	public List<Checklist> getByColaborador(@PathParam("cpf") Long cpf) {
		return service.getByColaborador(cpf);
	}
	
	@GET
	@Path("/perguntas")
	public List<Pergunta> getPerguntas() {
		return service.getPerguntas();
	}
	
	@DELETE
	@Path("{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if (service.delete(codigo)) {
			return Response.Ok("Checklist deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar checklist");
		}
	}
}
