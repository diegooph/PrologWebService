package br.com.empresa.oprojeto.webservice.rest;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.empresa.oprojeto.models.indicador.IndicadorHolder;
import br.com.empresa.oprojeto.models.util.DateUtils;
import br.com.empresa.oprojeto.webservice.services.IndicadorService;

@Path("/indicadores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class IndicadorResource {
	private IndicadorService service = new IndicadorService();
	
	@POST
	@Path("/byPeriodo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public IndicadorHolder getIndicadoresByPeriodo(
			@FormParam("dataInicial") long dataInicial, 
			@FormParam("dataFinal") long dataFinal, 
			@FormParam("cpf") long cpf) {
		return service.getIndicadoresByPeriodo(DateUtils.toLocalDate(new Date(dataInicial)), 
				DateUtils.toLocalDate(new Date(dataFinal)), cpf);
	}
}
