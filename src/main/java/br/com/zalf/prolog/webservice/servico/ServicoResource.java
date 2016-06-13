package br.com.zalf.prolog.webservice.servico;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.zalf.prolog.models.pneu.servico.PlacaServicoHolder;
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
	@Path("/veiculo/{placaVeiculo}")
	@Secured
	public ServicoHolder getServicosByPlaca(
			@PathParam("placaVeiculo") String placa){
		return service.getServicosByPlaca(placa);
	}

}
