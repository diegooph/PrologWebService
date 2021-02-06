package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/servicos")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        targetVersionCode = 115,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class ServicoResource {

    private final ServicoService service = new ServicoService();

    @POST
    @Secured(permissions = Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM)
    @Path("/conserto/{codUnidade}")
    public Response fechaServico(@HeaderParam("Authorization") @Required final String userToken,
                                 @PathParam("codUnidade") @Required final Long codUnidade,
                                 @Required final Servico servico) throws ProLogException {
        return service.fechaServico(userToken, codUnidade, servico);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/{codUnidade}/{codServico}")
    public Servico getServicoByCod(@PathParam("codUnidade") @Required final Long codUnidade,
                                   @PathParam("codServico") @Required final Long codServico) {
        return service.getServicoByCod(codUnidade, codServico);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/abertos/{codUnidade}/totais")
    public ServicosAbertosHolder getQuantidadeServicosAbertos(@PathParam("codUnidade") @Required final Long codUnidade,
                                                              @QueryParam("agrupamento") @Required final String agrupamento) {
        return service.getQuantidadeServicosAbertosVeiculo(codUnidade, agrupamento);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/abertos/veiculos/{placaVeiculo}/completo")
    public ServicoHolder getServicoHolder(@Required final ServicoHolderBuscaFiltro filtro) {
        return service.getServicoHolder(filtro);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/abertos/veiculos/{placaVeiculo}")
    public List<Servico> getServicosAbertosByPlaca(@PathParam("placaVeiculo") @Required final String placa,
                                                   @QueryParam("tipoServico") @Optional final String tipoServico) {
        return service.getServicosAbertosByPlaca(placa, tipoServico);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/fechados/{codUnidade}/totais")
    public ServicosFechadosHolder getQuantidadeServicosFechados(@PathParam("codUnidade") @Required final Long codUnidade,
                                                                @QueryParam("dataInicial") @Required final long dataInicial,
                                                                @QueryParam("dataFinal") @Required final long dataFinal,
                                                                @QueryParam("agrupamento") @Required final String agrupamento) {
        return service.getQuantidadeServicosFechados(codUnidade, dataInicial, dataFinal, agrupamento);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/fechados/{codUnidade}")
    public List<Servico> getServicosFechados(@PathParam("codUnidade") @Required final Long codUnidade,
                                             @QueryParam("dataInicial") @Required final long dataInicial,
                                             @QueryParam("dataFinal") @Required final long dataFinal) {
        return service.getServicosFechados(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/fechados/{codUnidade}/pneus/{codPneu}")
    public List<Servico> getServicosFechadosPneu(@PathParam("codUnidade") @Required final Long codUnidade,
                                                 @PathParam("codPneu") @Required final Long codPneu,
                                                 @QueryParam("dataInicial") @Required final long dataInicial,
                                                 @QueryParam("dataFinal") @Required final long dataFinal) {
        return service.getServicosFechadosPneu(codUnidade, codPneu, dataInicial, dataFinal);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/fechados/{codUnidade}/veiculos/{placaVeiculo}")
    public List<Servico> getServicosFechadosVeiculo(@PathParam("codUnidade") @Required final Long codUnidade,
                                                    @PathParam("placaVeiculo") @Required final String placaVeiculo,
                                                    @QueryParam("dataInicial") @Required final long dataInicial,
                                                    @QueryParam("dataFinal") @Required final long dataFinal) {
        return service.getServicosFechadosVeiculo(codUnidade, placaVeiculo, dataInicial, dataFinal);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.OrdemServico.Pneu.VISUALIZAR, Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/{codServico}/veiculos/{placaVeiculo}")
    public VeiculoServico getVeiculoAberturaServico(
            @HeaderParam("Authorization") @Required final String userToken,
            @PathParam("codServico") @Required final Long codServico,
            @PathParam("placaVeiculo") @Required final String placaVeiculo) throws ProLogException {
        return service.getVeiculoAberturaServico(userToken, codServico, placaVeiculo);
    }
}