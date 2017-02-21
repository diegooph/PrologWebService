package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.gente.solicitacao_folga.SolicitacaoFolga;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
import java.util.List;

@Path("/solicitacaoFolga")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SolicitacaoFolgaResource {

	private SolicitacaoFolgaService service = new SolicitacaoFolgaService();
	
	@POST
	@Secured(permissions = Pilares.Gente.SolicitacaoFolga.NOVA_SOLICITACAO)
	public AbstractResponse insert(SolicitacaoFolga solicitacaoFolga) {
		return service.insert(solicitacaoFolga);
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
	@Secured(permissions = Pilares.Gente.SolicitacaoFolga.FEEDBACK_SOLICITACAO)
	public Response update(SolicitacaoFolga solicitacaoFolga) {
		if (service.update(solicitacaoFolga)) {
			return Response.Ok("Solicitação atualizada com sucesso");
		} else {
			return Response.Error("Erro ao atualizar a solicitação");
		}
	}
	
	@DELETE
	@Secured(permissions = Pilares.Gente.SolicitacaoFolga.NOVA_SOLICITACAO)
	@Path("{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if(service.delete(codigo)){
			return Response.Ok("Solicitação deletada com sucesso");
		}else{
			return Response.Error("Erro ao deletar a solicitação");
		}
	}
		
}
