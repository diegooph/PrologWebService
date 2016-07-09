package br.com.zalf.prolog.webservice.gente.calendario;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/calendario")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CalendarioResource {
	private CalendarioService service = new CalendarioService();

	@GET
	@Secured
	@Path("/{cpf}")
	public List<Evento> getEventosByCpf(
			@PathParam("cpf") Long cpf){
		return service.getEventosByCpf(cpf);
	}
}
