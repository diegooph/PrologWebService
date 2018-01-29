package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.ResumoServicos;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.sql.SQLException;
import java.util.List;

@Path("/pneus/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioPneuResource {
	private final RelatorioPneuService service = new RelatorioPneuService();

	@GET
	@Path("/resumoSulcos")
	@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
	public List<Faixa> getQtPneusByFaixaSulco(
			@QueryParam("codUnidades") List<String> codUnidades,
			@QueryParam("status") List<String> status){
		return service.getQtPneusByFaixaSulco(codUnidades, status);
	}

	@GET
	@Path("/resumoPressao")
	@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
	public List<Faixa> getQtPneusByFaixaPressao(
			@QueryParam("codUnidades") List<String> codUnidades,
			@QueryParam("status") List<String> status){
		return service.getQtPneusByFaixaPressao(codUnidades, status);
	}

	@GET
	@Path("/aderencia/{codUnidade}/{ano}/{mes}")
	@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
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
											   @QueryParam("dataFinal") long dataFinal) throws RuntimeException{
		return outputStream -> service.getPrevisaoTrocaCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Path("/previsao-trocas/{codUnidade}/report")
	@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
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
														  @QueryParam("dataFinal") long dataFinal) throws RuntimeException{
		return outputStream -> service.getPrevisaoTrocaConsolidadoCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Path("/previsao-trocas/consolidados/{codUnidade}/report")
	@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
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
												 @QueryParam("dataFinal") long dataFinal) throws RuntimeException{
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
	@Path("/afericoes/resumo/pneus/{codUnidade}/csv")
	public StreamingOutput getDadosUltimaAfericaoCsv(@PathParam("codUnidade") Long codUnidade)  throws RuntimeException{
		return outputStream -> service.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
	}

	@GET
	@Path("/afericoes/resumo/pneus/{codUnidade}/report")
	@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
	public Report getDadosUltimaAfericaoReport(@PathParam("codUnidade") Long codUnidade) {
		return service.getDadosUltimaAfericaoReport(codUnidade);
	}

	@GET
	@Path("/servicos/estratificacao/fechados/{codUnidade}/report")
	public Report getEstratificacaoServicosFechadosReport(@PathParam("codUnidade") Long codUnidade,
														  @QueryParam("dataInicial") long dataInicial,
														  @QueryParam("dataFinal") long dataFinal) {
		return service.getEstratificacaoServicosFechadosReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Path("/servicos/estratificacao/fechados/{codUnidade}/csv")
	public StreamingOutput getEstratificacaoServicosFechadosCsv(@PathParam("codUnidade") Long codUnidade,
																@QueryParam("dataInicial") long dataInicial,
																@QueryParam("dataFinal") long dataFinal) throws RuntimeException{
		return outputStream -> service.getEstratificacaoServicosFechadosCsv(outputStream, codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Path("/pneus-descartados/{codUnidade}/report")
	public Report getEstratificacaoServicosFechadosReport(@PathParam("codUnidade") @Required Long codUnidade,
														  @QueryParam("dataInicial") @Required Long dataInicial,
														  @QueryParam("dataFinal") @Required Long dataFinal) {
		return service.getPneusDescartadosReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Path("/pneus-descartados/{codUnidade}/csv")
	public StreamingOutput getEstratificacaoServicosFechadosCsv(@PathParam("codUnidade") @Required Long codUnidade,
																@QueryParam("dataInicial") @Required Long dataInicial,
																@QueryParam("dataFinal") @Required Long dataFinal) throws RuntimeException{
		return outputStream -> service.getPneusDescartadosCsv(outputStream, codUnidade, dataInicial, dataFinal);
	}

	/**
	 * @deprecated in v0.0.11. Use {@link RelatorioPneuResource#getAderenciaPlacasReport(Long, long, long)} (Long, long)} instead
	 */
	@GET
	@Path("/aderencia/placas/{codUnidade}/report")
	@Deprecated
	public Report DEPRECATED_ADERENCIA_REPORT(@PathParam("codUnidade") Long codUnidade,
											  @QueryParam("dataInicial") long dataInicial,
											  @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
	}

	/**
	 * @deprecated in v0.0.11. Use {@link RelatorioPneuResource#getAderenciaPlacasCsv(Long, long, long)} (Long, long)} instead
	 */
	@GET
	@Path("/aderencia/placas/{codUnidade}/csv")
	@Produces("application/csv")
	@Deprecated
	public StreamingOutput DEPRECATED_ADERENCIA_CSV(@PathParam("codUnidade") Long codUnidade,
													@QueryParam("dataInicial") long dataInicial,
													@QueryParam("dataFinal") long dataFinal) throws RuntimeException{
		return outputStream -> service.getAerenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}
}