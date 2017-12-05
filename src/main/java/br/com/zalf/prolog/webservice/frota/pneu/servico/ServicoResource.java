package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.PlacaServicoHolder;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.ServicoHolder;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.ServicosFechadosHolder;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/servico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ServicoResource {

	private ServicoService service = new ServicoService();

	@POST
	@Secured(permissions = Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM)
	@Path("/conserto/{codUnidade}")
	public Response insertManutencao(Servico servico, @PathParam("codUnidade") Long codUnidade) {
		if (service.insertManutencao(servico, codUnidade)) {
			return Response.ok("Serviço consertado com sucesso.");
		} else {
			return Response.error("Erro ao marcar o item como consertado.");
		}
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/{codUnidade}")
	public PlacaServicoHolder getConsolidadoServicos(@PathParam("codUnidade") Long codUnidade) {
		return service.getConsolidadoListaVeiculos(codUnidade);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/{codUnidade}/{placaVeiculo}")
	public ServicoHolder getServicosByPlaca(
			@PathParam("placaVeiculo") String placa,
			@PathParam("codUnidade") Long codUnidade) {
		return service.getServicosByPlaca(placa, codUnidade);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/abertos/{placaVeiculo}/{tipoServico}")
	public List<Servico> getServicosAbertosByPlaca(
			@PathParam("placaVeiculo") String placa,
			@PathParam("tipoServico") String tipoServico) {
		return service.getServicosAbertosByPlaca(placa, tipoServico);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/fechados/{codUnidade}/totais")
	public ServicosFechadosHolder getQuantidadeServicosFechados(@PathParam("codUnidade") Long codUnidade,
																@QueryParam("dataInicial") long dataInicial,
																@QueryParam("dataFinal") long dataFinal,
																@QueryParam("agrupamento") String agrupamento) {
		return service.getQuantidadeServicosFechados(codUnidade, dataInicial, dataFinal, agrupamento);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/fechados/{codUnidade}")
	public List<Servico> getServicosFechados(@PathParam("codUnidade") Long codUnidade,
											 @QueryParam("dataInicial") long dataInicial,
											 @QueryParam("dataFinal") long dataFinal) {
		return service.getServicosFechados(codUnidade, dataInicial, dataFinal);
	}
}