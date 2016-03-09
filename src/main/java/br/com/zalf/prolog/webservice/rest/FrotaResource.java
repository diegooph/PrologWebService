package br.com.zalf.prolog.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
import br.com.zalf.prolog.webservice.services.FrotaService;


@Path("/frota")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class FrotaResource {
	private FrotaService service = new FrotaService();

	@POST
	@Path("/itensQuebrados")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<ManutencaoHolder> getManutencaoHolder(
			@FormParam("cpf") Long cpf,
			@FormParam("token") String token,
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("limit") int limit, 
			@FormParam("offset") long offset,
			@FormParam("isAbertos") boolean isAbertos){
		return service.getManutencaoHolder(cpf, token, codUnidade, limit, offset, isAbertos);
	}
	
	@POST
	@Path("/consertaItem")
	public boolean consertaItem(Request<?> request){
		return service.consertaItem(request);
	}
}