package br.com.zalf.prolog.webservice.frota.servico;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.frota.ItemManutencao;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

@Deprecated
@Path("/frota")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ServicoResource {
	private ServicoService service = new ServicoService();

	@GET
	@Secured
	@Path("/itensQuebrados/{codUnidade}")
	public List<ManutencaoHolder> getManutencaoHolder(
			@PathParam("codUnidade") Long codUnidade,
			@QueryParam("limit") int limit, 
			@QueryParam("offset") long offset,
			@QueryParam("isAbertos") boolean isAbertos){
		return service.getManutencaoHolder(codUnidade, limit, offset, isAbertos);
	}

	@POST
	@Secured
	@Path("/consertaItem")
	public Response consertaItem(ItemManutencao itemManutencao){
		if(service.consertaItem(itemManutencao)){
			return Response.Ok("Item consertado com sucesso");
		}else{
			return Response.Error("Problema ao consertar o item");
		}
	}
}