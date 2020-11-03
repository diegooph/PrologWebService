package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@ConsoleDebugLog
@Path("/checklists/ordens-servicos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        /* A partir da versão 68 o App coleta e envia a data/hora de início e fim de resolução dos itens de O.S. */
        targetVersionCode = 68,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public final class OrdemServicoResource {
    @NotNull
    private final OrdemServicoService service = new OrdemServicoService();

    @GET
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/listagem")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<OrdemServicoListagem> getOrdemServicoListagem(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
            @QueryParam("placaVeiculo") @Optional final String placaVeiculo,
            @QueryParam("statusOrdemServico") @Optional final StatusOrdemServico statusOrdemServico,
            @QueryParam("limit") @Required final int limit,
            @QueryParam("offset") @Required final int offset) throws ProLogException {
        return service.getOrdemServicoListagem(
                codUnidade,
                codTipoVeiculo,
                placaVeiculo,
                statusOrdemServico,
                limit,
                offset);
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/listagem-qtd-itens-placa")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<QtdItensPlacaListagem> getQtdItensPlacaListagem(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codTipoVeiculo") @Optional final Long codTipoVeiculo,
            @QueryParam("placaVeiculo") @Optional final String placaVeiculo,
            @QueryParam("statusItens") @Optional final StatusItemOrdemServico statusItens,
            @QueryParam("limit") @Required final int limit,
            @QueryParam("offset") @Required final int offset) throws ProLogException {
        return service.getQtdItensPlacaListagem(
                codUnidade,
                codTipoVeiculo,
                placaVeiculo,
                statusItens,
                limit,
                offset);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/resolucao-ordem-servico")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codOrdemServico") @Required final Long codOrdemServico) throws ProLogException {
        return service.getHolderResolucaoOrdemServico(codUnidade, codOrdemServico);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/resolucao-itens-ordem-servico")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @QueryParam("placaVeiculo") @Required final String placaVeiculo,
            @QueryParam("prioridade") @Optional final PrioridadeAlternativa prioridade,
            @QueryParam("statusItens") @Optional final StatusItemOrdemServico statusItens,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final int offset) throws ProLogException {
        return service.getHolderResolucaoItensOrdemServico(placaVeiculo, prioridade, statusItens, limit, offset);
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/resolucao-multiplos-itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @QueryParam("codUnidade") @Optional final Long codUnidade,
            @QueryParam("codOrdemServico") @Optional final Long codOrdemServico,
            @QueryParam("placaVeiculo") @Optional final String placaVeiculo,
            @QueryParam("statusItens") @Optional final StatusItemOrdemServico statusItens) throws ProLogException {
        return service
                .getHolderResolucaoMultiplosItens(codUnidade, codOrdemServico, placaVeiculo, statusItens);
    }

    @POST
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/resolver-item")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response resolverItem(@HeaderParam("Authorization") @Required final String token,
                                 @Required final ResolverItemOrdemServico item) throws ProLogException {
        return service.resolverItem(token, item);
    }

    @POST
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/resolver-multiplos-itens")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response resolverItens(@HeaderParam("Authorization") @Required final String token,
                                  @Required final ResolverMultiplosItensOs itensResolucao) throws ProLogException {
        return service.resolverItens(token, itensResolucao);
    }
}