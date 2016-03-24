package br.com.zalf.prolog.webservice.rest;

import java.sql.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.SolicitacaoFolga;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.auth.Secured;
import br.com.zalf.prolog.webservice.services.SolicitacaoFolgaService;

@Path("/solicitacaoFolga")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SolicitacaoFolgaResource {
	private SolicitacaoFolgaService service = new SolicitacaoFolgaService();
	
	@POST
	public Response insert(SolicitacaoFolga solicitacaoFolga) {
		if (service.insert(solicitacaoFolga)) {
			return Response.Ok("Solicitação de folga inserida com sucesso");
		} else {
			return Response.Error("Erro ao inserir solicitação de folga");
		}
	}
	
	@POST
	@Path("/colaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getByColaborador(@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getByColaborador(cpf, token);
	}
	
	@POST
	@Secured
	@Path("/getAll")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getAll(
			@FormParam("dataIncial") long dataInicial,
			@FormParam("dataFinal") long dataFinal,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("codEquipe") String codEquipe, 
			@FormParam("status") String status){
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)), codUnidade, codEquipe, status, null);
	}
	
	@POST
	@Secured
	@Path("/getAllByColaborador")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<SolicitacaoFolga> getAll(
			@FormParam("dataIncial") long dataInicial,
			@FormParam("dataFinal") long dataFinal,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("codEquipe") String codEquipe, 
			@FormParam("status") String status,
			@FormParam("cpfColaborador") Long cpfColaborador){
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)), codUnidade, codEquipe, status, cpfColaborador);
	}
	
	@PUT
	//@Path("/update")
	@Secured
	public Response update(SolicitacaoFolga solicitacaoFolga) {
		if (service.update(solicitacaoFolga)) {
			return Response.Ok("Solicitação atualizada com sucesso");
		} else {
			return Response.Error("Erro ao atualizar a solicitação");
		}
	}
	
	@DELETE
	@Secured
	public Response delete(SolicitacaoFolga solicitacaoFolga){
		if(service.delete(solicitacaoFolga)){
			return Response.Ok("Solicitação deletada com sucesso");
		}else{
			return Response.Error("Erro ao deletar a solicitação");
		}
	}
		
}
