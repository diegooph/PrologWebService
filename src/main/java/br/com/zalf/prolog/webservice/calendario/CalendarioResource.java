package br.com.zalf.prolog.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Evento;
import br.com.zalf.prolog.webservice.services.CalendarioService;

@Path("/calendario")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CalendarioResource {
	private CalendarioService service = new CalendarioService();

	@POST
	@Path("/getByCpf")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Evento> getEventosByCpf(
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token){
		return service.getEventosByCpf(cpf, token);
	}
}
