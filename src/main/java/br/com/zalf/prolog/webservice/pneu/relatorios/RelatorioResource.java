package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.pneu.relatorios.Faixa;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/pneus/relatorios/")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {
	
	RelatorioService service = new RelatorioService();
	
	@GET
	@Secured
	@Path("/resumoSulcos/{codUnidade}/{status}")
	public List<Faixa> getQtPneusByFaixaSulco(
			@PathParam("codUnidade") String codUnidade,
			@PathParam("status") String status){
		return service.getQtPneusByFaixaSulco(codUnidade, status);
	}
	
}
	
	



