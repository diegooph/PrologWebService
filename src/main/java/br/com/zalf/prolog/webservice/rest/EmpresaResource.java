package br.com.zalf.prolog.webservice.rest;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.webservice.services.EmpresaService;

@Path("/empresa")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EmpresaResource {
	
	EmpresaService service = new EmpresaService();
	
	@POST
	@Path("/getEquipesByCodUnidade")
	public List<Equipe> getEquipesByCodUnidade(Request<Equipe> request) throws SQLException{
		return service.getEquipesByCodUnidade(request);
	}

}
