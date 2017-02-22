package br.com.zalf.prolog.webservice.pneu.servico;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.pneu.servico.PlacaServicoHolder;
import br.com.zalf.prolog.frota.pneu.servico.Servico;
import br.com.zalf.prolog.frota.pneu.servico.ServicoHolder;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/servico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ServicoResource {

	private ServicoService service = new ServicoService();

	@GET
	@Secured(permissions = { Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM })
	@Path("/{codUnidade}")
	public PlacaServicoHolder getConsolidadoServicos(@PathParam("codUnidade") Long codUnidade){
		return service.getConsolidadoListaVeiculos(codUnidade);
	}

	@GET
	@Secured(permissions = { Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM })
	@Path("/{codUnidade}/{placaVeiculo}")
	public ServicoHolder getServicosByPlaca(
			@PathParam("placaVeiculo") String placa,
			@PathParam("codUnidade") Long codUnidade){
		return service.getServicosByPlaca(placa, codUnidade);
	}

	@GET
	@Secured(permissions = { Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM })
	@Path("/abertos/{placaVeiculo}/{tipoServico}")
	public List<Servico> getServicosAbertosByPlaca(
			@PathParam("placaVeiculo") String placa,
			@PathParam("tipoServico") String tipoServico){
		return service.getServicosAbertosByPlaca(placa, tipoServico);
	}

	@POST
	@Secured(permissions = Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM )
	@Path("/conserto/{codUnidade}")
	public Response insertManutencao(Servico servico, @PathParam("codUnidade") Long codUnidade, @HeaderParam("Authorization") String tokenHeader){
		String token = tokenHeader.substring("Bearer".length()).trim();
		if(service.insertManutencao(servico, codUnidade, token)){
			return Response.Ok("Serviço consertado com sucesso.");
		}else{
			return Response.Error("Erro ao marcar o item como consertado.");
		}
	}

}
