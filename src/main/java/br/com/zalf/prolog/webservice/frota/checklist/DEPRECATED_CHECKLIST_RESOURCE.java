package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
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
		checklist.setData(new Date(System.currentTimeMillis()));
		final Long codChecklist = service.insert(checklist, userToken);
		if (codChecklist != null) {
			return Response.ok("Checklist inserido com sucesso");
		} else {
			return Response.error("Erro ao inserir checklist");
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
	public Checklist getByCod(@PathParam("codigo") Long codigo, @HeaderParam("Authorization") String userToken) {
		return service.getByCod(codigo, userToken);
	}

	@GET
	@Path("/colaborador/{cpf}")
	@Secured(permissions = {Pilares.Frota.Checklist.VISUALIZAR_TODOS, Pilares.Frota.Checklist.REALIZAR})
	public List<Checklist> getByColaborador(
			@PathParam("cpf") Long cpf,
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@HeaderParam("Authorization") String userToken) {
		return service.getByColaborador(cpf, limit, offset, false, userToken);
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
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@HeaderParam("Authorization") String userToken) {
		return service.getAll(dataInicial, dataFinal, equipe, codUnidade, placa, limit, offset, false, userToken);
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
			@HeaderParam("Authorization") String userToken) {

		// Por conta da integração com o AvaCorp, vamos forçar que os usuários de Santa Cruz do Sul não possam utilizar
		// esse path e atualizem o app para utilizar os paths: checklists/novo/{codUnidade}/{codModelo}/{placa}/saida
		// e checklists/novo/{codUnidade}/{codModelo}/{placa}/saida.
		if (codUnidade.equals(4L)) {
			throw new IllegalStateException("É preciso atualizar o aplicativo para usar a nova versão do checklist");
		}
		return service.getNovoChecklistHolder(codUnidade, codModelo, placa, Checklist.TIPO_SAIDA, userToken);
	}

	/**
	 * @deprecated in v0.0.10 use {@link #getAll(Long, String, String, long, long, int, long, String)} instead
	 */
	@GET
	@Path("/recentes/{codUnidade}/{equipe}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	@Deprecated
	public List<Checklist> DEPRECATED_GET_ALL_UNIDADE(
			@PathParam("equipe") String equipe,
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("limit") int limit,
			@QueryParam("offset") long offset,
			@HeaderParam("Authorization") String userToken) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return service.getAll(
				calendar.getTimeInMillis(),
				System.currentTimeMillis(),
				equipe,
				codUnidade,
				"%",
				limit,
				offset,
				false,
				userToken);
	}
}