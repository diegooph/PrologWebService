package br.com.zalf.prolog.webservice.rest;

import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.dashboard.DashSeguranca;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.auth.Secured;
import br.com.zalf.prolog.webservice.services.DashService;

@Path("/dashboard")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashResource {

	DashService service = new DashService();
	
	@POST
	@Secured
	@Path("/seguranca")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public DashSeguranca getDashSeguranca(
			@FormParam("dataInicial") long dataInicial,
			@FormParam("dataFinal") long dataFinal, 
			@FormParam("codUnidade")Long codUnidade, 
			@FormParam("equipe") String equipe) throws SQLException{
		return service.getDashSeguranca(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)), codUnidade, equipe);
	}
	
	
}
