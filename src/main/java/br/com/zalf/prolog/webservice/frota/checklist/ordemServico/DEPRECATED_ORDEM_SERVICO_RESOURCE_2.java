package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OLD.ConsertoMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OLD.ManutencaoHolder;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OLD.OrdemServico;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 */
@DebugLog
@Path("/checklist/ordens-servicos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
public class DEPRECATED_ORDEM_SERVICO_RESOURCE_2 {
    private final OrdemServicoService service = new OrdemServicoService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens/conserto")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM)
    public Response consertaItem(ItemOrdemServico item) throws ProLogException {
        service.consertaItem(item);
        return Response.ok("Item consertado com sucesso");
    }

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens/conserto-multiplos")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM)
    public Response consertaItens(ConsertoMultiplosItensOs itensConserto) throws ProLogException {
        service.consertaItens(itensConserto);
        return Response.ok("Itens consertados com sucesso");
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<OrdemServico> getOs(@PathParam("codUnidade") Long codUnidade,
                                    @PathParam("tipoVeiculo") String tipoVeiculo,
                                    @PathParam("placa") String placa,
                                    @PathParam("status") String status,
                                    @QueryParam("limit") Integer limit,
                                    @QueryParam("offset") Long offset) throws Throwable {
        return service.getOs(placa, status, codUnidade, tipoVeiculo, limit, offset);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<ItemOrdemServico> getItensOrdemServico(@QueryParam("placa") @Required String placa,
                                                       @QueryParam("status-itens") @Required String statusItens,
                                                       @QueryParam("prioridade-itens") @Optional String prioridade,
                                                       @QueryParam("limit") @Optional Integer limit,
                                                       @QueryParam("offset") @Optional Long offset)
            throws ProLogException {
        return service.getItensOsManutencaoHolder(placa, statusItens, prioridade, limit, offset);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codOs}/unidades/{codUnidade}/itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<ItemOrdemServico> getItensOrdemServico(@PathParam("codOs") @Required Long codOs,
                                                       @PathParam("codUnidade") @Required Long codUnidade,
                                                       @QueryParam("statusItemOs") @Optional String statusItemOs)
            throws ProLogException {
        return service.getItensOs(codOs, codUnidade, statusItemOs);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{codUnidade}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("codUnidade") @Required Long codUnidade,
                                                            @QueryParam("codTipoVeiculo") @Optional Long codTipoVeiculo,
                                                            @QueryParam("placaVeiculo") @Optional String placaVeiculo,
                                                            @QueryParam("itensEmAberto") @Required Boolean itensEmAberto,
                                                            @QueryParam("limit") @Required int limit,
                                                            @QueryParam("offset") @Required int offset)
            throws ProLogException {
        return service.getResumoManutencaoHolder(codUnidade, codTipoVeiculo, placaVeiculo, itensEmAberto, limit, offset);
    }
}