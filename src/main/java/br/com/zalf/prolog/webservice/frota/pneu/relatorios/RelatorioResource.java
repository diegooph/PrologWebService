package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.Report;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.Pneu;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.sql.SQLException;
import java.util.List;

@Path("/pneus/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {

	private RelatorioService service = new RelatorioService();

	@GET
	@Path("/resumoSulcos")
	public List<Faixa> getQtPneusByFaixaSulco(
			@QueryParam("codUnidades") List<String> codUnidades,
			@QueryParam("status") List<String> status){
		return service.getQtPneusByFaixaSulco(codUnidades, status);
	}

	@GET
	@Path("/resumoPressao")
	public List<Faixa> getQtPneusByFaixaPressao(
			@QueryParam("codUnidades") List<String> codUnidades,
			@QueryParam("status") List<String> status){
		return service.getQtPneusByFaixaPressao(codUnidades, status);
	}

	@GET
	@Path("/aderencia/{codUnidade}/{ano}/{mes}")
	public List<Aderencia> getAderenciaByUnidade(
			@PathParam("ano") int ano,
			@PathParam("mes") int mes,
			@PathParam("codUnidade") Long codUnidade){
		return service.getAderenciaByUnidade(ano, mes, codUnidade);
	}

	@GET
	@Path("resumoServicos/{ano}/{mes}")
	public List<ResumoServicos> getResumoServicosByUnidades(
			@PathParam("ano") int ano,
			@PathParam("mes") int mes,
			@QueryParam("codUnidades") List<String> codUnidades){
		return service.getResumoServicosByUnidades(ano, mes, codUnidades);
	}

	@GET
	@Path("/previsao-trocas/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getPrevisaoTrocaCsv(@PathParam("codUnidade") Long codUnidade,
											   @QueryParam("dataInicial") long dataInicial,
											   @QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getPrevisaoTrocaCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Path("/previsao-trocas/{codUnidade}/report")
	public Report getPrevisaoTrocaReport(@PathParam("codUnidade") Long codUnidade,
										 @QueryParam("dataInicial") long dataInicial,
										 @QueryParam("dataFinal") long dataFinal){
		return service.getPrevisaoTrocaReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Path("/previsao-trocas/consolidados/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getPrevisaoTrocaConsolidadoCsv(@PathParam("codUnidade") Long codUnidade,
														  @QueryParam("dataInicial") long dataInicial,
														  @QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getPrevisaoTrocaConsolidadoCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Path("/previsao-trocas/consolidados/{codUnidade}/report")
	public Report getPrevisaoTrocaConsolidadoReport(@PathParam("codUnidade") Long codUnidade,
													@QueryParam("dataInicial") long dataInicial,
													@QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getPrevisaoTrocaConsolidadoReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Path("/aderencias/placas/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getAderenciaPlacasCsv(@PathParam("codUnidade") Long codUnidade,
												 @QueryParam("dataInicial") long dataInicial,
												 @QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getAerenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Path("/aderencias/placas/{codUnidade}/report")
	public Report getAderenciaPlacasReport(@PathParam("codUnidade") Long codUnidade,
										   @QueryParam("dataInicial") long dataInicial,
										   @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Path("/faixas/sulcos/{codEmpresa}/{codUnidade}")
	public List<Pneu> getPneusByFaixaSulco(@QueryParam("inicioFaixa") double inicioFaixa,
										   @QueryParam("fimFaixa") double fimFaixa,
										   @PathParam("codEmpresa") Long codEmpresa,
										   @PathParam("codUnidade") String codUnidade,
										   @QueryParam("limit") long limit,
										   @QueryParam("offset") long offset) {
		return service.getPneusByFaixaSulco(inicioFaixa, fimFaixa, codEmpresa, codUnidade, limit, offset);
	}

	@GET
	@Secured
	@Path("/afericoes/resumo/pneus/{codUnidade}/csv")
	public StreamingOutput getDadosUltimaAfericaoCsv(@PathParam("codUnidade") Long codUnidade) {
		return outputStream -> service.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
	}

	@GET
	@Secured
	@Path("/afericoes/resumo/pneus/{codUnidade}/report")
	public Report getDadosUltimaAfericaoReport(@PathParam("codUnidade") Long codUnidade) {
		return service.getDadosUltimaAfericaoReport(codUnidade);
	}

	/**
	 * @deprecated in v0.0.11. Use {@link RelatorioResource#getAderenciaPlacasCsv(Long, long, long)} (Long, long)} instead
	 */
	@GET
	@Path("/aderencia/placas/{codUnidade}/csv")
	@Produces("application/csv")
	@Deprecated
	public StreamingOutput DEPRECATED_ADERENCIA_CSV(@PathParam("codUnidade") Long codUnidade,
													@QueryParam("dataInicial") long dataInicial,
													@QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getAerenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	/**
	 * @deprecated in v0.0.11. Use {@link RelatorioResource#getAderenciaPlacasReport(Long, long, long)} (Long, long)} instead
	 */
	@GET
	@Path("/aderencia/placas/{codUnidade}/report")
	@Deprecated
	public Report DEPRECATED_ADERENCIA_REPORT(@PathParam("codUnidade") Long codUnidade,
											  @QueryParam("dataInicial") long dataInicial,
											  @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
	}
}