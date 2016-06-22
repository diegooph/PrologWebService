package br.com.zalf.prolog.webservice.seguranca.dashboard;

import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.dashboard.DashSeguranca;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/dashboard")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashSegurancaResource {

	DashSegurancaService service = new DashSegurancaService();
	
	@GET
	@Secured
	@Path("/seguranca")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public DashSeguranca getDashSeguranca(
			@QueryParam("dataInicial") long dataInicial,
			@QueryParam("dataFinal") long dataFinal, 
			@QueryParam("codUnidade")Long codUnidade, 
			@QueryParam("equipe") String equipe) throws SQLException{
		return service.getDashSeguranca(DateUtils.toLocalDate(new Date(dataInicial)), DateUtils.toLocalDate(new Date(dataFinal)), codUnidade, equipe);
	}
	
	
}
