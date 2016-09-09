package br.com.zalf.prolog.webservice.entrega.indicadorOlder;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.indicador.older.IndicadorHolder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/indicadores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class IndicadorResource {
	private IndicadorService service = new IndicadorService();
	
	@POST
	@Path("/byPeriodo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void getIndicadoresByPeriodo(
			@FormParam("dataInicial") long dataInicial,
			@FormParam("dataFinal") long dataFinal,
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token) {
//		return service.getIndicadoresByPeriodo(DateUtils.toLocalDate(new Date(dataInicial)),
//				DateUtils.toLocalDate(new Date(dataFinal)), cpf, token);
	}
}
