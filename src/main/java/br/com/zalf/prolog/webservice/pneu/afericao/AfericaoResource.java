package br.com.zalf.prolog.webservice.pneu.afericao;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.pneu.afericao.Afericao;
import br.com.zalf.prolog.frota.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.frota.pneu.afericao.SelecaoPlacaAfericao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.LogBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/afericao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AfericaoResource {

	AfericaoService service = new AfericaoService();

	@POST
	@Secured
	@LogBody
	@Path("/{codUnidade}")
	public Response insert(Afericao afericao,
			@PathParam("codUnidade") Long codUnidade) {
		afericao.setDataHora(new Date(System.currentTimeMillis()));
		if (service.Insert(afericao, codUnidade)) {
			return Response.Ok("Aferição inserida com sucesso");
		} else {
			return Response.Error("Erro ao inserir aferição");
		}
	}

	@GET
	@Path("/{placaVeiculo}")
	@Secured
	public NovaAfericao getNovaAfericao(@PathParam("placaVeiculo") String placa){
		return service.getNovaAfericao(placa);
	}
		
	@GET
	@Secured
	@Path("/listaAfericao/{codUnidade}")
	public SelecaoPlacaAfericao getSelecaoPlacasAfericao(
			@PathParam("codUnidade") Long codUnidade){
		return service.getSelecaoPlacaAfericao(codUnidade);
	}
	
	@GET
	@Secured
	@Path("/getAll")
	public List<Afericao> getAfericoesByCodUnidadeByPlaca(
			@QueryParam("codUnidades") List<String> codUnidades, 
			@QueryParam("placas") List<String> placas, 
			@QueryParam("limit") long limit,
			@QueryParam("offset") long offset){
		return service.getAfericoesByCodUnidadeByPlaca(codUnidades, placas, limit, offset);
	}
	
	@GET
	@Secured
	public Afericao getByCod (@QueryParam("codAfericao") Long codAfericao, @QueryParam("codUnidade") Long codUnidade){
		return service.getByCod(codAfericao, codUnidade);
	}
}
