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
import br.com.zalf.prolog.models.gsd.Gsd;
import br.com.zalf.prolog.webservice.services.GsdService;

@Path("/gsd")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GsdResource {
	private GsdService service = new GsdService();
	
	@POST
	public Response insert(Gsd gsd) {
		gsd.setDataHora(new Date(System.currentTimeMillis()));
		if (service.insert(gsd)) {
			return Response.Ok("Gsd inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir gsd");
		}
	}
	
//	@PUT
//	public Response update(Gsd gsd) {
//		if (service.update(gsd)) {
//			return Response.Ok("Gsd atualizado com sucesso");
//		} else {
//			return Response.Error("Erro ao atualizar o gsd");
//		}
//	}
	
//	@GET
//	@Path("{codigo}")
//	public Gsd getByCod(@PathParam("codigo") Long codigo) {
//		return service.getByCod(codigo);
//	}
	
	@GET
	@Path("/perguntas")
	public List<Pergunta> getPerguntas() {
		return service.getPerguntas();
	}
	
	
//	@GET
//	public List<Gsd> getAll() {
//		return service.getAll();
//	}
	

//	@POST
//	@Path("/colaborador")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	public List<Gsd> getByColaborador(@FormParam("cpf") Long cpf,
//			@FormParam("token") String token) {
//		return service.getByColaborador(cpf, token);
//	}
	
	@POST
	@Path("/avaliador")
	public List<Gsd> getByAvaliador(@FormParam("cpf") Long cpf,
			@FormParam("token") String token) {
		return service.getByAvaliador(cpf, token);
	}
	
//	@DELETE
//	@Path("{codigo}")
//	public Response delete(@PathParam("codigo") Long codigo) {
//		if (service.delete(codigo)) {
//			return Response.Ok("Gsd deletado com sucesso");
//		} else {
//			return Response.Error("Falha ao deletar gsd");
//		}
//	}
}
