package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.Filtros;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 */

@Path("/checklist/ordemServico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OrdemServicoResource {
    private final OrdemServicoService service = new OrdemServicoService();

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/consertaItem/{placa}")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM)
    public Response consertaItem(ItemOrdemServico item,
                                 @PathParam("placa") String placa) {
        if (service.consertaItem(item, placa)) {
            return Response.ok("Serviço consertado com sucesso");
        } else {
            return Response.error("Erro ao consertar o item");
        }
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<OrdemServico> getOs(@PathParam("codUnidade") Long codUnidade,
                                    @PathParam("tipoVeiculo") String tipoVeiculo,
                                    @PathParam("placa") String placa,
                                    @PathParam("status") String status,
                                    @QueryParam("limit") Integer limit,
                                    @QueryParam("offset") Long offset) {
        return service.getOs(placa, status, codUnidade, tipoVeiculo, limit, offset);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{placa}/{status}/{prioridade}")
    @Secured(permissions = {Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<ItemOrdemServico> getItensOsManutencaoHolder(@PathParam("placa") String placa,
                                                             @PathParam("status") String status,
                                                             @PathParam("prioridade") String prioridade,
                                                             @QueryParam("limit") int limit,
                                                             @QueryParam("offset") long offset) {
        return service.getItensOsManutencaoHolder(placa, status, limit, offset, prioridade);
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


    /**
     * @deprecated at 2018-08-13. Use {@link #getResumoManutencaoHolder(Long, Long, String, Boolean, int, int)} instead.
     */
    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    @Deprecated
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("codUnidade") Long codUnidade,
                                                            @PathParam("tipoVeiculo") String codTipo,
                                                            @PathParam("placa") String placa,
                                                            @PathParam("status") String status,
                                                            @QueryParam("limit") int limit,
                                                            @QueryParam("offset") int offset) throws ProLogException {
        final Long codTipoVeiculo = Filtros.isFiltroTodos(codTipo) ? null : Long.parseLong(codTipo);
        final String placaVeiculo = Filtros.isFiltroTodos(placa) ? null : placa;
        final boolean itensEmAberto = status.equals(ItemOrdemServico.Status.PENDENTE.asString());
        return service.getResumoManutencaoHolder(codUnidade, codTipoVeiculo, placaVeiculo, itensEmAberto, limit, offset);
    }
}