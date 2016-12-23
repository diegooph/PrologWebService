package br.com.zalf.prolog.webservice.entrega.relatorioOlder;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.relatorio.older.ConsolidadoHolder;
import br.com.zalf.prolog.webservice.empresa.EmpresaService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {
	private RelatorioService service = new RelatorioService();
	
	@POST
	@Path("/getFiltros")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Empresa> getFiltros(
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token){
		return new EmpresaService().getFiltros(cpf);
	}
	
	@POST
	@Path("/byEquipe")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ConsolidadoHolder getRelatorioByPeriodo(
			@FormParam("dataInicial") long dataInicial, 
			@FormParam("dataFinal") long dataFinal, 
			@FormParam("equipe") String equipe,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token) {
		System.out.println("Inciial: " + new Date(dataInicial));
		System.out.println("Final: " + new Date(dataFinal));
		
		return service.getRelatorioByPeriodo(DateUtils.toLocalDate(new Date(dataInicial)),
				DateUtils.toLocalDate(new Date(dataFinal)),equipe, codUnidade, cpf, token);
	}
}

