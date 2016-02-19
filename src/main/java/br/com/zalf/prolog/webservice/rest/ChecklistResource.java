package br.com.zalf.prolog.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
	
//	@PUT
//	public Response update(Checklist checklist) {
//		if (service.update(checklist)) {
//			return Response.Ok("Checklist atualizado com sucesso");
//		} else {
//			return Response.Error("Erro ao atualizar o checklist");
//		}
//	}
//	
//	@GET
//	@Path("{codigo}")
//	public Checklist getByCod(@PathParam("codigo") Long codigo) {
//		return service.getByCod(codigo);
//	}
//	
//	@GET
//	public List<Checklist> getAll() {
//		System.out.println("CHAMOU");
//		return service.getAll();
//	}
	
//	@GET
//	@Path("/exceto/colaborador/{cpf}")
//	public List<Checklist> getAllExcetoColaborador(Long cpf, long offset) {
//		return service.getAllExcetoColaborador(cpf, offset);
//	}
	
	@POST
	@Path("/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Checklist> getByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token,
			@FormParam("offset") long offset) {
		return service.getByColaborador(cpf, token, offset);
	}
	
	@GET
	@Path("/perguntas")
	public List<Pergunta> getPerguntas() {
		return service.getPerguntas();
	}
	
//	@DELETE
//	@Path("{codigo}")
//	public Response delete(@PathParam("codigo") Long codigo) {
//		if (service.delete(codigo)) {
//			return Response.Ok("Checklist deletado com sucesso");
//		} else {
//			return Response.Error("Erro ao deletar checklist");
//		}
//	}
}
