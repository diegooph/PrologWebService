package br.com.zalf.prolog.webservice.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.auth.Secured;
import br.com.zalf.prolog.webservice.services.ChecklistService;

@Path("/checklist")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource {
	private ChecklistService service = new ChecklistService();
	
	@GET
	@Secured
	@Path("/perguntas/{codUnidade}")
	public List<PerguntaRespostaChecklist> getPerguntas(@PathParam("codUnidade") Long codUnidade){
		return service.getPerguntas(codUnidade);
	}
	
	@POST
	@Secured
	public Response insert(Checklist checklist) {
		checklist.setData(new Date(System.currentTimeMillis()));
		if (service.insert(checklist)) {
			return Response.Ok("Checklist inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir checklist");
		}
	}
	
	@GET
	@Secured
	@Path("/liberacao/{codUnidade}")
	public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(@PathParam("codUnidade")Long codUnidade){
		return service.getStatusLiberacaoVeiculos(codUnidade);
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
	
	@GET
	@Secured
	@Path("{codUnidade}/{equipe}")
	public List<Checklist> getAll(
			@QueryParam("dataInicial") long dataInicial, 
			@QueryParam("dataFinal") long dataFinal,
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("limit")long limit,
			@QueryParam("offset") long offset) {
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)),DateUtils.toLocalDate(new Date(dataFinal)), equipe, codUnidade, limit, offset);
	}
	
//	@GET
//	@Path("/exceto/colaborador/{cpf}")
//	public List<Checklist> getAllExcetoColaborador(Long cpf, long offset) {
//		return service.getAllExcetoColaborador(cpf, offset);
//	}
	
//	@POST
//	@Path("/unidade")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	public List<Checklist> getAllByCodUnidade(
//			@FormParam("cpf") Long cpf,
//			@FormParam("token") String token,
//			@FormParam("codUnidade") Long codUnidade,
//			@FormParam("dataInicial") long dataInicial,
//			@FormParam("dataFinal") long dataFinal,
//			@FormParam("limit") int limit,
//			@FormParam("offset") long offset) {
//		return service.getAllByCodUnidade(cpf, token, codUnidade, DateUtils.toLocalDate(new Date(dataInicial)), 
//				DateUtils.toLocalDate(new Date(dataFinal)), limit, offset);
//	}	
//	
	@GET
	@Secured
	@Path("/colaborador/{cpf}")
	public List<Checklist> getByColaborador(
			@PathParam("cpf") Long cpf, 
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset) {
		return service.getByColaborador(cpf, limit, offset);
	}
	
	@GET
	@Secured
	@Path("/novo/{codUnidade}")
	public NovoChecklistHolder getNovoChecklistHolder(@PathParam("codUnidade") Long codUnidade){
		return service.getNovoChecklistHolder(codUnidade);
	}
		
//	@GET
//	@Path("/perguntas")
//	public List<Pergunta> getPerguntas() {
//		return service.getPerguntas();
//	}
	
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
