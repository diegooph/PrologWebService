package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 */
@ConsoleDebugLog
@Path("/v2/checklist/old-ordens-servicos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
@AppVersionCodeHandler(
        targetVersionCode = 62,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class DEPRECATED_ORDEM_SERVICO_RESOURCE_2 {
    private final DEPRECATED_ORDEM_SERVICO_SERVICE_2 service = new DEPRECATED_ORDEM_SERVICO_SERVICE_2();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens/conserto")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response consertaItem(final ItemOrdemServico item) throws ProLogException {
        service.consertaItem(item);
        return Response.ok("Item consertado com sucesso");
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens/conserto-multiplos")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response consertaItens(final ConsertoMultiplosItensOs itensConserto) throws ProLogException {
        service.consertaItens(itensConserto);
        return Response.ok("Itens consertados com sucesso");
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<OrdemServico> getOs(@PathParam("codUnidade") final Long codUnidade,
                                    @PathParam("tipoVeiculo") final String tipoVeiculo,
                                    @PathParam("placa") final String placa,
                                    @PathParam("status") final String status,
                                    @QueryParam("limit") final Integer limit,
                                    @QueryParam("offset") final Long offset) throws Throwable {
        return service.getOs(placa, status, codUnidade, tipoVeiculo, limit, offset);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ItemOrdemServico> getItensOrdemServico(@QueryParam("placa") @Required final String placa,
                                                       @QueryParam("status-itens") @Required final String statusItens,
                                                       @QueryParam("prioridade-itens") @Optional final String prioridade,
                                                       @QueryParam("limit") @Optional final Integer limit,
                                                       @QueryParam("offset") @Optional final Long offset)
            throws ProLogException {
        return service.getItensOsManutencaoHolder(placa, statusItens, prioridade, limit, offset);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codOs}/unidades/{codUnidade}/itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ItemOrdemServico> getItensOrdemServico(@PathParam("codOs") @Required final Long codOs,
                                                       @PathParam("codUnidade") @Required final Long codUnidade,
                                                       @QueryParam("statusItemOs") @Optional final String statusItemOs)
            throws ProLogException {
        return service.getItensOs(codOs, codUnidade, statusItemOs);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{codUnidade}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("codUnidade") @Required final Long codUnidade,
                                                            @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
                                                            @QueryParam("placaVeiculo") @Optional final String placaVeiculo,
                                                            @QueryParam("itensEmAberto") @Required final Boolean itensEmAberto,
                                                            @QueryParam("limit") @Required final int limit,
                                                            @QueryParam("offset") @Required final int offset)
            throws ProLogException {
        return service.getResumoManutencaoHolder(codUnidade, codTipoVeiculo, placaVeiculo, itensEmAberto, limit, offset);
    }
}