package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.checklist.Checklist;
import br.com.zalf.prolog.frota.checklist.ModeloChecklist;
import br.com.zalf.prolog.frota.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.frota.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.L;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/checklist")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChecklistResource {
	private ChecklistService service = new ChecklistService();


	@GET
	@Secured
	@Path("/urlImagens/{codUnidade}/{codFuncao}")
	public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") Long codUnidade, @PathParam("codFuncao") Long codFuncao){
		return service.getUrlImagensPerguntas(codUnidade, codFuncao);
	}

	@POST
	@Secured
	public Response insert(Checklist checklist) {
		L.d("ChecklistResource", "Chamou o resource");
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

	@PUT
	@Secured
	public Response update(Checklist checklist) {
		if (service.update(checklist)) {
			return Response.Ok("Checklist atualizado com sucesso");
		} else {
			return Response.Error("Erro ao atualizar o checklist");
		}
	}

	@GET
	@Secured
	@Path("{codigo}")
	public Checklist getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}

	@GET
	@Secured
	@Path("{codUnidade}/{equipe}/{placa}")
	public List<Checklist> getAll(
			@QueryParam("dataInicial") long dataInicial, 
			@QueryParam("dataFinal") long dataFinal,
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("equipe") String equipe,
			@PathParam("placa") String placa,
			@QueryParam("limit")long limit,
			@QueryParam("offset") long offset){
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), equipe, codUnidade, placa, limit, offset);
	}

	@GET
	@Secured
	@Path("/recentes/{codUnidade}/{equipe}")
	public List<Checklist> getAllByCodUnidade(
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("limit")long limit,
			@QueryParam("offset") long offset) {
		LocalDate dataInicial = LocalDate.of(2016, Month.JANUARY, 01);
		Date datainicial = java.sql.Date.valueOf(dataInicial);
		return service.getAll(DateUtils.toLocalDate(datainicial),
				DateUtils.toLocalDate(new Date(System.currentTimeMillis())), equipe, codUnidade,"%", limit, offset);
	}

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
	@Path("/modeloPlacas/{codUnidade}/{codFuncaoColaborador}")
	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
			@PathParam("codUnidade") Long codUnidade, 
			@PathParam("codFuncaoColaborador") Long codFuncao){
		return service.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
	}
	
	@GET
	@Secured
	@Path("/novo/{codUnidade}/{codModelo}/{placa}")
	public NovoChecklistHolder getNovoChecklistHolder(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("codModelo") Long codModelo,
			@PathParam("placa") String placa){
		return service.getNovoChecklistHolder(codUnidade, codModelo, placa);
	}

	@DELETE
	@Secured
	@Path("{codigo}")
	public Response delete(@PathParam("codigo") Long codigo) {
		if (service.delete(codigo)) {
			return Response.Ok("Checklist deletado com sucesso");
		} else {
			return Response.Error("Erro ao deletar checklist");
		}
	}
}
