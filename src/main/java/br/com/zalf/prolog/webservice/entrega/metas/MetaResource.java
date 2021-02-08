package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/v2/metas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MetaResource{
	@NotNull
	private final MetaService service = new MetaService();

	@GET
	@Secured (permissions = Pilares.Entrega.Meta.VISUALIZAR)
	@Path("/{codUnidade}")
	public javax.ws.rs.core.Response getByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
		return service.getByCodUnidade(codUnidade);
	}
	
	@PUT
	@Secured (permissions = Pilares.Entrega.Meta.EDITAR)
	@Path("/{codUnidade}")
	public Response update(Metas metas, @PathParam("codUnidade") Long codUnidade) {
		service.update(metas, codUnidade);
		return Response.ok("Metas atualizadas com sucesso");
	}
}