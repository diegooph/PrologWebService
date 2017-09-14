package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.commons.util.L;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/checklist")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
public class DEPRECATED_CHECKLIST_RESOURCE {

	private ChecklistService service = new ChecklistService();

	@POST
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	public Response insert(Checklist checklist, @HeaderParam("Authorization") String userToken) {
		L.d("DEPRECATED_CHECKLIST_RESOURCE", "Chamou o resource");
		checklist.setData(new Date(System.currentTimeMillis()));
		if (service.insert(checklist, userToken)) {
			return Response.Ok("Checklist inserido com sucesso");
		} else {
			return Response.Error("Erro ao inserir checklist");
		}
	}

	@GET
	@Path("/urlImagens/{codUnidade}/{codFuncao}")
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") Long codUnidade,
											   @PathParam("codFuncao") Long codFuncao){
		return service.getUrlImagensPerguntas(codUnidade, codFuncao);
	}

	@GET
	@Path("/liberacao/{codUnidade}")
	@Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
	public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(@PathParam("codUnidade")Long codUnidade) {
		return service.getStatusLiberacaoVeiculos(codUnidade);
	}

	@GET
	@Path("{codigo}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	public Checklist getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}

	@GET
	@Path("/colaborador/{cpf}")
	@Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
	public List<Checklist> getByColaborador(
			@PathParam("cpf") Long cpf,
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset) {
		return service.getByColaborador(cpf, limit, offset);
	}

	@GET
	@Path("{codUnidade}/{equipe}/{placa}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	public List<Checklist> getAll(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("equipe") String equipe,
			@PathParam("placa") String placa,
			@QueryParam("dataInicial") long dataInicial,
			@QueryParam("dataFinal") long dataFinal,
			@QueryParam("limit")long limit,
			@QueryParam("offset") long offset) {
		return service.getAll(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)), equipe, codUnidade, placa, limit, offset);
	}

	@GET
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	@Path("/modeloPlacas/{codUnidade}/{codFuncaoColaborador}")
	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("codFuncaoColaborador") Long codFuncao,
			@HeaderParam("Authorization") String userToken) {
		return service.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao, userToken);
	}

	@GET
	@Path("/novo/{codUnidade}/{codModelo}/{placa}")
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	public NovoChecklistHolder getNovoChecklistHolder(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("codModelo") Long codModelo,
			@PathParam("placa") String placa,
			@HeaderParam("Authorization") String userToken){
		return service.getNovoChecklistHolder(codUnidade, codModelo, placa, userToken);
	}

	/**
	 * @deprecated in v0.0.10 use {@link #getAll(Long, String, String, long, long, long, long)} instead
	 */
	@GET
	@Path("/recentes/{codUnidade}/{equipe}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	@Deprecated
	public List<Checklist> DEPRECATED_GET_ALL_UNIDADE(
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("limit")long limit,
			@QueryParam("offset") long offset) {
		LocalDate dataInicial = LocalDate.of(2016, Month.JANUARY, 01);
		Date datainicial = java.sql.Date.valueOf(dataInicial);
		return service.getAll(DateUtils.toLocalDate(datainicial),
				DateUtils.toLocalDate(new Date(System.currentTimeMillis())), equipe, codUnidade,"%", limit, offset);
	}
}