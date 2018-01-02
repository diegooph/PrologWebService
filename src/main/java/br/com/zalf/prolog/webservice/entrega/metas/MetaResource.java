package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/metas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MetaResource{

	private final MetaService service = new MetaService();

	@GET
	@Secured (permissions = Pilares.Entrega.Meta.VISUALIZAR)
	@Path("/{codUnidade}")
	public Metas getByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getByCodUnidade(codUnidade);
	}
	
	@PUT
	@Secured (permissions = Pilares.Entrega.Meta.EDITAR)
	@Path("/{codUnidade}")
	public Response update(Metas metas, @PathParam("codUnidade") Long codUnidade) {
		if (service.update(metas, codUnidade)) {
			return Response.ok("Meta atualizada com sucesso");
		} else {
			return Response.error("Erro ao atualizar a meta");
		}
	}
}