package br.com.zalf.prolog.webservice.seguranca.dashboard;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.seguranca.dashboard.DashSeguranca;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.Date;

@Path("/dashboard")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashSegurancaResource {

	private DashSegurancaService service = new DashSegurancaService();
	
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
