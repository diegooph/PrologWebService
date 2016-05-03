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
import br.com.zalf.prolog.models.util.DateUtils;
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
			@FormParam("limit") int limit,
			@FormParam("offset") long offset,
			@FormParam("latitude") double latitude,
			@FormParam("longitude") double longitude,
			@FormParam("isOrderByDate") boolean isOrderByDate) {
		return service.getByColaborador(cpf, token, limit, offset, latitude, longitude, isOrderByDate);
	}
	
	@POST
	@Path("/unidade")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Relato> getAllByUnidade(@FormParam("dataInicial") long dataInicial, 
			@FormParam("dataFinal") long dataFinal, 
			@FormParam("equipe") String equipe,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token,
			@FormParam("limit") long limit,
	@FormParam("offset") long offset) {
		return service.getAllByUnidade(DateUtils.toLocalDate(new Date(dataInicial)), 
				DateUtils.toLocalDate(new Date(dataFinal)),equipe, codUnidade, cpf, token, limit, offset);
	}
	
	@POST
	@Path("/exceto/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Relato> getAllExcetoColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token,
			@FormParam("limit") int limit,
			@FormParam("offset") long offset,
			@FormParam("latitude") double latitude,
			@FormParam("longitude") double longitude,
			@FormParam("isOrderByDate") boolean isOrderByDate) {
		return service.getAllExcetoColaborador(cpf, token, limit, offset, latitude, longitude, isOrderByDate);
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
