package br.com.zalf.prolog.webservice.metas;

import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.entrega.produtividade.Metas;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/meta")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MetaResource{
	private MetaService service = new MetaService();

	@POST
	@Path("/byCodUnidade")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Metas<?>> getByCodUnidade(
			@FormParam("codUnidade") Long codUnidade,
			@FormParam("cpf") Long cpf, 
			@FormParam("token") String token) {
		return service.getByCodUnidade(codUnidade, cpf, token);
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