package br.com.zalf.prolog.webservice.pneu.servico;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.pneu.servico.PlacaServicoHolder;
import br.com.zalf.prolog.models.pneu.servico.Servico;
import br.com.zalf.prolog.models.pneu.servico.ServicoHolder;
import br.com.zalf.prolog.webservice.auth.Secured;


@Path("/servico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ServicoResource {
	
	ServicoService service = new ServicoService();
	
	@GET
	@Path("/{codUnidade}")
	@Secured
	public PlacaServicoHolder getConsolidadoServicos(@PathParam("codUnidade") Long codUnidade){
		return service.getConsolidadoListaVeiculos(codUnidade);
	}
	
	@GET
	@Path("/{codUnidade}/{placaVeiculo}")
	@Secured
	public ServicoHolder getServicosByPlaca(
			@PathParam("placaVeiculo") String placa,
			@PathParam("codUnidade") Long codUnidade){
		return service.getServicosByPlaca(placa, codUnidade);
	}
	
	@POST
	@Secured
	@Path("/conserto/{codUnidade}")
	public Response insertManutencao(Servico servico, @PathParam("codUnidade") Long codUnidade){
		if(service.insertManutencao(servico, codUnidade)){
			return Response.Ok("Serviço consertado com sucesso.");
		}else{
			return Response.Error("Erro ao marcar o item como consertado.");
		}
	}

}
