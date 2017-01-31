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
	@Path("/previsaoCompras/{codUnidade}/csv")
	@Produces("application/csv")
	public StreamingOutput getPrevisaoCompraCsv(@PathParam("codUnidade") Long codUnidade,
                                                @QueryParam("dataInicial") Long dataInicial,
                                                @QueryParam("dataFinal") Long dataFinal){
		return outputStream -> service.getPrevisaoCompraCsv(codUnidade, dataInicial, dataFinal, outputStream);
	}

	@GET
	@Secured
	@Path("/previsaoCompras/{codUnidade}/report")
	public Report getPrevisaoCompraReport(@PathParam("codUnidade") Long codUnidade,
                                          @QueryParam("dataInicial") Long dataInicial,
                                          @QueryParam("dataFinal") Long dataFinal) throws SQLException{
		return service.getPrevisaoCompraReport(codUnidade, dataInicial, dataFinal);
	}
	
}
	
	



