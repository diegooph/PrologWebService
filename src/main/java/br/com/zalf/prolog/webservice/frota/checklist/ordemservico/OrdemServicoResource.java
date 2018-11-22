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
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
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
@DebugLog
@Path("/checklist/ordens-servicos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
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
    public List<QtdItensPlacaListagem> getOrdemServicoListagemPlacas(
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("placaVeiculo") @Optional final String placaVeiculo,
            @QueryParam("statusItemOrdemServico") @Optional final StatusItemOrdemServico statusItemOrdemServico,
            @QueryParam("limit") @Required final int limit,
            @QueryParam("offset") @Required final int offset) throws ProLogException {
        return service.getQtdItensPlacaListagem(
                codUnidade,
                placaVeiculo,
                statusItemOrdemServico,
                limit,
                offset);
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
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
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/resolucao-itens-ordem-servico")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public HolderResolucaoItensOrdemServico getHolderResolucaoOrdemServico(
            @QueryParam("placaVeiculo") @Required final String placaVeiculo,
            @QueryParam("prioridade") @Required final PrioridadeAlternativa prioridadeAlternativa) throws ProLogException {
        return service.getHolderResolucaoItensOrdemServico(placaVeiculo, prioridadeAlternativa);
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/resolver-item")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response resolverItem(ResolverItemOrdemServico item) throws ProLogException {
        service.resolverItem(item);
        return Response.ok("Item resolvido com sucesso");
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/resolver-multiplos-itens")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response resolverItens(ResolverMultiplosItensOs itensResolucao) throws ProLogException {
        service.resolverItens(itensResolucao);
        return Response.ok("Itens resolvidos com sucesso");
    }
}