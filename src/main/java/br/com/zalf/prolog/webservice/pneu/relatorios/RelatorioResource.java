package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.pneu.relatorios.Aderencia;
import br.com.zalf.prolog.models.pneu.relatorios.Faixa;
import br.com.zalf.prolog.models.pneu.relatorios.ResumoServicos;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

@Path("/pneus/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {
	
	RelatorioService service = new RelatorioService();
	
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
	
}
	
	



