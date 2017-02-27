package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.checklist.Checklist;
import br.com.zalf.prolog.frota.checklist.ModeloChecklist;
import br.com.zalf.prolog.frota.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.frota.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.permissao.pilares.Pilares;
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

	@POST
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
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
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	@Path("/urlImagens/{codUnidade}/{codFuncao}")
	public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") Long codUnidade, @PathParam("codFuncao") Long codFuncao){
		return service.getUrlImagensPerguntas(codUnidade, codFuncao);
	}

	@GET
	@Secured(permissions = Pilares.Frota.FarolStatusPlacas.VISUALIZAR)
	@Path("/liberacao/{codUnidade}")
	public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(@PathParam("codUnidade")Long codUnidade){
		return service.getStatusLiberacaoVeiculos(codUnidade);
	}

	@GET
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR)
	@Path("{codigo}")
	public Checklist getByCod(@PathParam("codigo") Long codigo) {
		return service.getByCod(codigo);
	}

	@GET
	@Secured(permissions = { Pilares.Frota.Checklist.VISUALIZAR, Pilares.Frota.Checklist.REALIZAR })
	@Path("/colaborador/{cpf}")
	public List<Checklist> getByColaborador(
			@PathParam("cpf") Long cpf,
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset) {
		return service.getByColaborador(cpf, limit, offset);
	}

	@GET
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR)
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
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	@Path("/modeloPlacas/{codUnidade}/{codFuncaoColaborador}")
	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("codFuncaoColaborador") Long codFuncao){
		return service.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
	}

	@GET
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	@Path("/novo/{codUnidade}/{codModelo}/{placa}")
	public NovoChecklistHolder getNovoChecklistHolder(
			@PathParam("codUnidade") Long codUnidade,
			@PathParam("codModelo") Long codModelo,
			@PathParam("placa") String placa){
		return service.getNovoChecklistHolder(codUnidade, codModelo, placa);
	}

	/**
	 * @deprecated in v0.0.10 use {@link #getAll(long, long, Long, String, String, long, long)} instead
	 */
	@GET
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR)
	@Path("/recentes/{codUnidade}/{equipe}")
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