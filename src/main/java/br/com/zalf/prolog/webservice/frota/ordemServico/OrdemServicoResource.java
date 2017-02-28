package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.frota.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.frota.checklist.os.ManutencaoHolder;
import br.com.zalf.prolog.frota.checklist.os.OrdemServico;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;

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

    private OrdemServicoService service = new OrdemServicoService();

    @GET
    @Android
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

    @POST
    @Android
    @Path("/consertaItem/{codUnidade}/{placa}")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM)
    public Response consertaItem(ItemOrdemServico item,
                                 @PathParam("codUnidade") Long codUnidade,
                                 @PathParam("placa") String placa) {
        if (service.consertaItem(codUnidade, item, placa)) {
            return Response.Ok("Serviço consertado com sucesso");
        } else {
            return Response.Error("Erro ao consertar o item");
        }
    }

    @GET
    @Android
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
    @Android
    @Path("/manutencao/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.CONSERTAR_ITEM})
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("codUnidade") Long codUnidade,
                                                            @PathParam("tipoVeiculo") String codTipo,
                                                            @PathParam("placa") String placa,
                                                            @PathParam("status") String status,
                                                            @QueryParam("limit") int limit,
                                                            @QueryParam("offset") long offset) {
        return service.getResumoManutencaoHolder(placa, codTipo, codUnidade, limit, offset, status);
    }
}