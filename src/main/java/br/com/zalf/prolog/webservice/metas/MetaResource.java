package br.com.zalf.prolog.webservice.metas;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.entrega.indicador.Meta;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/metas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MetaResource{

	private MetaService service = new MetaService();

	@GET
	@Path("/{codUnidade}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Meta getByCodUnidade(
			@PathParam("codUnidade") Long codUnidade) {
		return service.getByCodUnidade(codUnidade);
	}
	
	@PUT
	public Response update(Meta meta, Long codUnidade) {
		if (service.update(meta, codUnidade)) {
			return Response.Ok("Meta atualizada com sucesso");
		} else {
			return Response.Error("Erro ao atualizar a meta");
		}
	}

}