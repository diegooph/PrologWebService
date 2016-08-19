package br.com.zalf.prolog.webservice.entrega.relatorio;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.Empresa;
import br.com.zalf.prolog.models.util.DateUtils;


@Path("/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RelatorioResource {
	private RelatorioService service = new RelatorioService();
	

	
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

