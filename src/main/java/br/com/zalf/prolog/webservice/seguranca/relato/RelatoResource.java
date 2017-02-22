package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.seguranca.relato.Relato;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/relatos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatoResource {

	private RelatoService service = new RelatoService();

	@POST
	@Secured(permissions = Pilares.Seguranca.Relato.REALIZAR)
	public Response insert(Relato relato) {
		relato.setDataDatabase(new Date(System.currentTimeMillis()));
		if (service.insert(relato)) {
			return Response.Ok("Relato inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir relato");
		}
	}

	@GET
	@Secured(permissions = { Pilares.Seguranca.Relato.REALIZAR, Pilares.Seguranca.Relato.VISUALIZAR,
	Pilares.Seguranca.Relato.CLASSIFICAR, Pilares.Seguranca.Relato.FECHAR})
	@Path("/{codigo}")
	public Relato getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}

	@GET
	@Secured(permissions = { Pilares.Seguranca.Relato.REALIZAR, Pilares.Seguranca.Relato.VISUALIZAR,
			Pilares.Seguranca.Relato.CLASSIFICAR, Pilares.Seguranca.Relato.FECHAR})
	@Path("/realizados/{cpf}/{status}")
	public List<Relato> getRealizadosByColaborador(@PathParam("cpf") Long cpf,
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@QueryParam("latitude") double latitude,
			@QueryParam("longitude") double longitude,
			@QueryParam("isOrderByDate") boolean isOrderByDate,
			@PathParam("status") String status) {
		return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status, "realizados");
	}

	@GET
	@Path("/{status}/colaborador/{cpf}")
	@Secured(permissions = { Pilares.Seguranca.Relato.REALIZAR, Pilares.Seguranca.Relato.VISUALIZAR,
			Pilares.Seguranca.Relato.CLASSIFICAR, Pilares.Seguranca.Relato.FECHAR})
	public List<Relato> getByColaborador(@PathParam("status") String status,
										 @PathParam("cpf") Long cpf,
										 @QueryParam("limit") int limit,
										 @QueryParam("offset") long offset,
										 @QueryParam("latitude") double latitude,
										 @QueryParam("longitude") double longitude,
										 @QueryParam("isOrderByDate") boolean isOrderByDate){
		return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate,
				status, "realizados" );
	}


	@GET
	@Secured
	@Path("/classificados/{cpf}/{status}")
	public List<Relato> getClassificadosByColaborador(@PathParam("cpf") Long cpf, 
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@QueryParam("latitude") double latitude,
			@QueryParam("longitude") double longitude,
			@QueryParam("isOrderByDate") boolean isOrderByDate,
			@PathParam("status") String status) {
		return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status, "classificados");
	}
	
	@GET
	@Secured(permissions = { Pilares.Seguranca.Relato.VISUALIZAR,
			Pilares.Seguranca.Relato.CLASSIFICAR, Pilares.Seguranca.Relato.FECHAR})
	@Path("/fechados/{cpf}/{status}")
	public List<Relato> getFechadosByColaborador(@PathParam("cpf") Long cpf, 
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@QueryParam("latitude") double latitude,
			@QueryParam("longitude") double longitude,
			@QueryParam("isOrderByDate") boolean isOrderByDate,
			@PathParam("status") String status) {
		return service.getRealizadosByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status, "fechados");
	}
	
	@GET
	@Secured(permissions = Pilares.Seguranca.Relato.REALIZAR)
	@Path("{status}/exceto/colaborador/{cpf}")
	public List<Relato> getAllExcetoColaborador(@PathParam("cpf") Long cpf, 
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@QueryParam("latitude") double latitude,
			@QueryParam("longitude") double longitude,
			@QueryParam("isOrderByDate") boolean isOrderByDate,
			@PathParam("status") String status) {
		return service.getAllExcetoColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status);
	}

	@GET
	@Secured(permissions = { Pilares.Seguranca.Relato.REALIZAR, Pilares.Seguranca.Relato.VISUALIZAR,
			Pilares.Seguranca.Relato.CLASSIFICAR, Pilares.Seguranca.Relato.FECHAR})
	@Path("/unidade")
	public List<Relato> getAllByUnidade(@QueryParam("dataInicial") long dataInicial, 
			@QueryParam("dataFinal") long dataFinal, 
			@QueryParam("equipe") String equipe,
			@QueryParam("codUnidade") Long codUnidade,
			@QueryParam("limit") long limit,
			@QueryParam("offset") long offset,
			@QueryParam("status") String status) {
		return service.getAllByUnidade(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)), equipe,
				codUnidade, limit, offset, status);
	}

	@GET
	@Secured(permissions = { Pilares.Seguranca.Relato.REALIZAR, Pilares.Seguranca.Relato.VISUALIZAR,
			Pilares.Seguranca.Relato.CLASSIFICAR, Pilares.Seguranca.Relato.FECHAR})
	@Path("/{codUnidade}/{status}")
	public List<Relato> getAll(@PathParam("codUnidade") Long codUnidade,
			@PathParam("status") String status,
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset) {
		return service.getAll(codUnidade, limit, offset, 0, 0, true, status);
	}

	@PUT
	@Secured(permissions = Pilares.Seguranca.Relato.CLASSIFICAR)
	@Path("/classificar")
	public Response classificaRelato(Relato relato){
		if(service.classificaRelato(relato)){
			return Response.Ok("Relato classificado com sucesso");
		}else{
			return Response.Error("Erro ao classificar o relato");
		}
	}

	@PUT
	@Secured(permissions = { Pilares.Seguranca.Relato.FECHAR, Pilares.Seguranca.Relato.CLASSIFICAR })
	@Path("/fechar")
	public Response fechaRelato(Relato relato){
		if(service.fechaRelato(relato)){
			return Response.Ok("Relato fechado com sucesso");
		}else{
			return Response.Error("Erro ao fechar o relato");
		}
	}

	@DELETE
	@Secured(permissions = Pilares.Seguranca.Relato.FECHAR)
	@Path("/{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if (service.delete(codigo)) {
			return Response.Ok("Relato deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar relato");
		}
	}
}
