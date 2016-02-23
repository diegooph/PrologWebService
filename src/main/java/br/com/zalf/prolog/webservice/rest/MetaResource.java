package br.com.zalf.prolog.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Metas;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.webservice.services.MetaService;

@Path("/meta")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MetaResource{
	private MetaService service = new MetaService();

	@POST
	@Path("/byCpf")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Metas<?>> getByCpf(
			@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getByCpf( cpf, token);
	}
	
	@PUT
	public Response updateByCod(Request<Metas> request) {
		if (service.updateByCod(request)) {
			return Response.Ok("Meta atualizada com sucesso");
		} else {
			return Response.Error("Erro ao atualizar a meta");
		}
	}

}