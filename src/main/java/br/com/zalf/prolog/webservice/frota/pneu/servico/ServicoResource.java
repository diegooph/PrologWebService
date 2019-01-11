package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/servicos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
		implementation = DefaultAppVersionCodeHandler.class,
		targetVersionCode = 64,
		versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
		actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class ServicoResource {

	private final ServicoService service = new ServicoService();

	@POST
	@Secured(permissions = Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM)
	@Path("/conserto/{codUnidade}")
	public Response fechaServico(@Required Servico servico,
								 @PathParam("codUnidade") @Required Long codUnidade) {
		if (service.fechaServico(servico, codUnidade)) {
			return Response.ok("Serviço consertado com sucesso.");
		} else {
			return Response.error("Erro ao marcar o item como consertado.");
		}
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/{codUnidade}/{codServico}")
	public Servico getServicoByCod(@PathParam("codUnidade") @Required Long codUnidade,
								   @PathParam("codServico") @Required Long codServico) {
		return service.getServicoByCod(codUnidade, codServico);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/abertos/{codUnidade}/totais")
	public ServicosAbertosHolder getQuantidadeServicosAbertos(@PathParam("codUnidade") @Required Long codUnidade,
															  @QueryParam("agrupamento") @Required String agrupamento) {
		return service.getQuantidadeServicosAbertosVeiculo(codUnidade, agrupamento);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/abertos/veiculos/{placaVeiculo}/completo")
	public ServicoHolder getServicoHolder(@PathParam("placaVeiculo") @Required String placa,
										  @QueryParam("codUnidade") @Required Long codUnidade) {
		return service.getServicoHolder(placa, codUnidade);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/abertos/veiculos/{placaVeiculo}")
	public List<Servico> getServicosAbertosByPlaca(@PathParam("placaVeiculo") @Required String placa,
												   @QueryParam("tipoServico") @Optional String tipoServico) {
		return service.getServicosAbertosByPlaca(placa, tipoServico);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/fechados/{codUnidade}/totais")
	public ServicosFechadosHolder getQuantidadeServicosFechados(@PathParam("codUnidade") @Required Long codUnidade,
																@QueryParam("dataInicial") @Required long dataInicial,
																@QueryParam("dataFinal") @Required long dataFinal,
																@QueryParam("agrupamento") @Required String agrupamento) {
		return service.getQuantidadeServicosFechados(codUnidade, dataInicial, dataFinal, agrupamento);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/fechados/{codUnidade}")
	public List<Servico> getServicosFechados(@PathParam("codUnidade") @Required Long codUnidade,
											 @QueryParam("dataInicial") @Required long dataInicial,
											 @QueryParam("dataFinal") @Required long dataFinal) {
		return service.getServicosFechados(codUnidade, dataInicial, dataFinal);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/fechados/{codUnidade}/pneus/{codPneu}")
	public List<Servico> getServicosFechadosPneu(@PathParam("codUnidade") @Required Long codUnidade,
												 @PathParam("codPneu") @Required Long codPneu,
												 @QueryParam("dataInicial") @Required long dataInicial,
												 @QueryParam("dataFinal") @Required long dataFinal) {
		return service.getServicosFechadosPneu(codUnidade, codPneu, dataInicial, dataFinal);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/fechados/{codUnidade}/veiculos/{placaVeiculo}")
	public List<Servico> getServicosFechadosVeiculo(@PathParam("codUnidade") @Required Long codUnidade,
													@PathParam("placaVeiculo") @Required String placaVeiculo,
													@QueryParam("dataInicial") @Required long dataInicial,
													@QueryParam("dataFinal") @Required long dataFinal) {
		return service.getServicosFechadosVeiculo(codUnidade, placaVeiculo, dataInicial, dataFinal);
	}

	@GET
	@Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
	@Path("/{codServico}/veiculos/{placaVeiculo}")
	public VeiculoServico getVeiculoAberturaServico(@PathParam("codServico") @Required Long codServico,
													@PathParam("placaVeiculo") @Required String placaVeiculo) {
		return service.getVeiculoAberturaServico(codServico, placaVeiculo);
	}
}