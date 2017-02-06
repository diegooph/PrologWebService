package br.com.zalf.prolog.webservice.pneu.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.frota.pneu.relatorio.Aderencia;
import br.com.zalf.prolog.frota.pneu.relatorio.Faixa;
import br.com.zalf.prolog.frota.pneu.relatorio.ResumoServicos;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.sql.SQLException;
import java.util.List;

@Path("/pneus/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {
	
	private RelatorioService service = new RelatorioService();
	
	@GET
	@Secured
	@Path("/resumoSulcos")
	public List<Faixa> getQtPneusByFaixaSulco(
			@QueryParam("codUnidades") List<String> codUnidades,
			@QueryParam("status") List<String> status){
		return service.getQtPneusByFaixaSulco(codUnidades, status);
	}
	
	@GET
	@Secured
	@Path("/resumoPressao")
	public List<Faixa> getQtPneusByFaixaPressao(
			@QueryParam("codUnidades") List<String> codUnidades,
			@QueryParam("status") List<String> status){
		return service.getQtPneusByFaixaPressao(codUnidades, status);
	}
	
	@GET
	@Secured
	@Path("/aderencia/{codUnidade}/{ano}/{mes}")
	public List<Aderencia> getAderenciaByUnidade(
			@PathParam("ano") int ano,
			@PathParam("mes") int mes,
			@PathParam("codUnidade") Long codUnidade){	
		return service.getAderenciaByUnidade(ano, mes, codUnidade);
	}

	@GET
	@Secured
	@Path("resumoServicos/{ano}/{mes}")
	public List<ResumoServicos> getResumoServicosByUnidades(
			@PathParam("ano") int ano,
			@PathParam("mes") int mes,
			@QueryParam("codUnidades") List<String> codUnidades){
		return service.getResumoServicosByUnidades(ano, mes, codUnidades);
	}

	@GET
	@Secured
	@Path("/previsao-compras/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getPrevisaoCompraCsv(@PathParam("codUnidade") Long codUnidade,
                                                @QueryParam("dataInicial") long dataInicial,
                                                @QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getPrevisaoCompraCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Secured
	@Path("/previsao-compras/{codUnidade}/report")
	public Report getPrevisaoCompraReport(@PathParam("codUnidade") Long codUnidade,
                                          @QueryParam("dataInicial") long dataInicial,
                                          @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getPrevisaoCompraReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Secured
	@Path("/previsao-compras/consolidados/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getPrevisaoCompraConsolidadoCsv(@PathParam("codUnidade") Long codUnidade,
												@QueryParam("dataInicial") long dataInicial,
												@QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getPrevisaoCompraConsolidadoCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Secured
	@Path("/previsao-compras/consolidados/{codUnidade}/report")
	public Report getPrevisaoCompraConsolidadoReport(@PathParam("codUnidade") Long codUnidade,
										  @QueryParam("dataInicial") long dataInicial,
										  @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getPrevisaoCompraConsolidadoReport(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Secured
	@Path("/aderencias/placas/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getAderenciaPlacasCsv(@PathParam("codUnidade") Long codUnidade,
														   @QueryParam("dataInicial") long dataInicial,
														   @QueryParam("dataFinal") long dataFinal){
		return outputStream -> service.getAerenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Secured
	@Path("/aderencia/placas/{codUnidade}/report")
	public Report getAderenciaPlacasReport(@PathParam("codUnidade") Long codUnidade,
													 @QueryParam("dataInicial") long dataInicial,
													 @QueryParam("dataFinal") long dataFinal) throws SQLException{
		return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
	}


	
}
	
	



