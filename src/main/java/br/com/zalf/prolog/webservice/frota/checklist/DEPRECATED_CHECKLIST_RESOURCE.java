package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloResource;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura.ChecklistMigracaoEstruturaSuporte;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Path("/checklist")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
@AppVersionCodeHandler(
		implementation = DefaultAppVersionCodeHandler.class,
		targetVersionCode = 80,
		versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
		actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public final class DEPRECATED_CHECKLIST_RESOURCE {

	private final ChecklistService service = new ChecklistService();

	@POST
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	public Response insert(@HeaderParam("Authorization") @Required final String userToken,
						   @Required final Checklist checklist) throws ProLogException {
		return null;
	}

	/**
	 * @deprecated at 09/03/2018. Use {@link ChecklistModeloResource} instead.
	 */
	@GET
	@Path("/urlImagens/{codUnidade}/{codFuncao}")
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	@Deprecated
	public List<String> getUrlImagensPerguntas(@PathParam("codUnidade") final Long codUnidade,
											   @PathParam("codFuncao") final Long codFuncao) throws ProLogException {
		return new ChecklistModeloService().getUrlImagensPerguntas(codUnidade, codFuncao);
	}

	@GET
	@Path("{codigo}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	public Checklist getByCod(@PathParam("codigo") final Long codigo, @HeaderParam("Authorization") final String userToken) {
		return service.getByCod(codigo, userToken);
	}

	@GET
	@Path("{codUnidade}/{equipe}/{placa}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	public List<Checklist> getAll(
			@PathParam("codUnidade") final Long codUnidade,
			@PathParam("equipe") final String equipe,
			@PathParam("placa") final String placa,
			@QueryParam("dataInicial") final long dataInicial,
			@QueryParam("dataFinal") final long dataFinal,
			@QueryParam("limit") final int limit,
			@QueryParam("offset") final long offset,
			@HeaderParam("Authorization") final String userToken) {
		return service.getAll(
				codUnidade,
				null,
				null,
				placa.equals("%") ? null : placa,
				dataInicial,
				dataFinal,
				limit,
				offset,
				false,
				userToken);
	}

	@GET
	@Path("/novo/{codUnidade}/{codModelo}/{placa}")
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	public NovoChecklistHolder getNovoChecklistHolder(
			@PathParam("codUnidade") final Long codUnidade,
			@PathParam("codModelo") final Long codModelo,
			@PathParam("placa") final String placa,
			@HeaderParam("Authorization") final String userToken) {

		// Por conta da integração com o AvaCorp, vamos forçar que os usuários da Avilan não possam utilizar
		// esse path e atualizem o app para utilizar os paths: checklists/novo/{codUnidade}/{codModelo}/{placa}/saida
		// e checklists/novo/{codUnidade}/{codModelo}/{placa}/saida.
		if (codUnidade.equals(4L) || codUnidade.equals(3L) || codUnidade.equals(2L)) {
			throw new IllegalStateException("É preciso atualizar o aplicativo para usar a nova versão do checklist");
		}

		throw new GenericException("Atualize o aplicativo para utilizar a nova versão do checklist");
	}

	/**
	 * @deprecated at 2019-08-18. Use {@link ChecklistModeloResource#getModelosSelecaoRealizacao(Long, Long, String)}
	 * instead.
	 */
	@GET
	@Secured(permissions = Pilares.Frota.Checklist.REALIZAR)
	@Path("/modeloPlacas/{codUnidade}/{codFuncaoColaborador}")
	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
			@PathParam("codUnidade") final Long codUnidade,
			@PathParam("codFuncaoColaborador") final Long codFuncao,
			@HeaderParam("Authorization") final String userToken) {
		// Esse método já está redirecionando para o novo Service.
		return ChecklistMigracaoEstruturaSuporte.toEstruturaAntigaSelecaoModelo(
				new ChecklistModeloService().getModelosSelecaoRealizacao(codUnidade, codFuncao, userToken));
	}

	/**
	 * @deprecated in v0.0.10 use {@link #getAll(Long, String, String, long, long, int, long, String)} instead
	 */
	@GET
	@Path("/recentes/{codUnidade}/{equipe}")
	@Secured(permissions = Pilares.Frota.Checklist.VISUALIZAR_TODOS)
	@Deprecated
	public List<Checklist> DEPRECATED_GET_ALL_UNIDADE(
			@PathParam("equipe") final String equipe,
			@PathParam("codUnidade") final Long codUnidade,
			@QueryParam("limit") final int limit,
			@QueryParam("offset") final long offset,
			@HeaderParam("Authorization") final String userToken) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return service.getAll(
				codUnidade,
				null,
				null,
				null,
				calendar.getTimeInMillis(),
				Now.getUtcMillis(),
				limit,
				offset,
				true,
				userToken);
	}
}