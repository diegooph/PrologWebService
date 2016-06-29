package br.com.zalf.prolog.webservice.pneu.afericao;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.pneu.afericao.Afericao;
import br.com.zalf.prolog.models.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.models.pneu.afericao.SelecaoPlacaAfericao;
import br.com.zalf.prolog.webservice.auth.Secured;

@Path("/afericao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AfericaoResource {

	AfericaoService service = new AfericaoService();

	@POST
	@Secured
	@Path("/{codUnidade}")
	public Response insert(Afericao afericao, 
			@PathParam("codUnidade") Long codUnidade) {
		System.out.println(new Gson().toJson(afericao));
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
}
