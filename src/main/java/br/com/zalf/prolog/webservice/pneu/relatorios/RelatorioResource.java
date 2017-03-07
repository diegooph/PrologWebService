package br.com.zalf.prolog.webservice.pneu.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.frota.pneu.relatorio.Aderencia;
import br.com.zalf.prolog.frota.pneu.relatorio.Faixa;
import br.com.zalf.prolog.frota.pneu.relatorio.ResumoServicos;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.sql.SQLException;
import java.util.List;

@Path("/pneus/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.Pneu.VISUALIZAR)
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
										 @QueryParam("dataFinal") long dataFinal) throws SQLException{
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
	@Path("/aderencia/placas/{codUnidade}/report")
	public Report getAderenciaPlacasReport(@PathParam("codUnidade") Long codUnidade,
										   @QueryParam("dataInicial") long dataInicial,
										   @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
	}
}