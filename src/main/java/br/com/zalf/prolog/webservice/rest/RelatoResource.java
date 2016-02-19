package br.com.zalf.prolog.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Relato;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.services.RelatoService;

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
	
//	@PUT
//	public Response update(Relato relato) {
//		if (service.update(relato)) {
//			return Response.Ok("Relato atualizado com sucesso");
//		} else {
//			return Response.Error("Erro ao atualizar o relato");
//		}
//	}
//	
//	@GET
//	public List<Relato> getAll() {
//		return service.getAll();
//	}
//	
//	@GET
//	@Path("{codigo}")
//	public Relato getByCod(@PathParam("codigo") Long codigo) {
//		return service.getByCod(codigo);
//	}
	
	@POST
	@Path("/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Relato> getByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token,
			@FormParam("offset") long offset,
			@FormParam("latitude") double latitude,
			@FormParam("longitude") double longitude) {
		return service.getByColaborador(cpf, token, offset, latitude, longitude);
	}
	
	@POST
	@Path("/exceto/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Relato> getAllExcetoColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token,
			@FormParam("offset") long offset,
			@FormParam("latitude") double latitude,
			@FormParam("longitude") double longitude) {
		return service.getAllExcetoColaborador(cpf, token, offset, latitude, longitude);
	}
	
//	@DELETE
//	@Path("{codigo}")
//	public Response delete(@PathParam("codigo") Long codigo) {
//		if (service.delete(codigo)) {
//			return Response.Ok("Relato deletado com sucesso");
//		} else {
//			return Response.Error("Erro ao deletar relato");
//		}
//	}
}
