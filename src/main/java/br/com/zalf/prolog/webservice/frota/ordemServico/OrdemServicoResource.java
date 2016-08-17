package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.models.checklist.os.ManutencaoHolder;
import br.com.zalf.prolog.models.checklist.os.OrdemServico;
import br.com.zalf.prolog.models.checklist.os.OsHolder;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.util.Android;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
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
    @Secured
    @Path("/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    public List<OrdemServico> getOs(@PathParam("placa") String placa,
                                    @PathParam("status") String status,
                                    @PathParam("codUnidade") Long codUnidade,
                                    @PathParam("tipoVeiculo") String tipoVeiculo,
                                    @QueryParam("limit") Integer limit,
                                    @QueryParam("offset") Long offset){

        return service.getOs(placa, status, null, codUnidade, tipoVeiculo, limit, offset);
    }

//    @GET
//    @Secured
//    @Path("/manutencao/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
//    public List<ManutencaoHolder> getManutencaoHolder (@PathParam("codUnidade") Long codUnidade,
//                                                       @QueryParam("limit") int limit,
//                                                       @QueryParam("offset") long offset,
//                                                       @PathParam("status") String status,
//                                                       @PathParam("placa") String placa,
//                                                       @PathParam("tipoVeiculo") String codTipo){
//        return service.getManutencaoHolder(placa, codTipo, codUnidade, limit, offset, status);
//    }

    @POST
    @Android
    @Secured
    @Path("/consertaItem/{codUnidade}")
    public Response consertaItem (@PathParam("codUnidade")Long codUnidade, ItemOrdemServico item){
        if (service.consertaItem(codUnidade, item)){
            return Response.Ok("Serviço consertado com sucesso");
        }else{
            return Response.Error("Erro ao consertar o item");
        }
    }

    @GET
    @Android
    @Secured
    @Path("/manutencao/{placa}/{status}/{prioridade}")
    public List<ItemOrdemServico> getItensOsManutencaoHolder(@PathParam("placa") String placa,
                                                             @QueryParam("limit") int limit,
                                                             @QueryParam("offset") long offset,
                                                             @PathParam("status") String status,
                                                             @PathParam("prioridade") String prioridade){
        return service.getItensOsManutencaoHolder(placa, status, limit, offset, prioridade);
    }

    @GET
    @Android
    @Secured
    @Path("/manutencao/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("placa") String placa,
                                                            @PathParam("tipoVeiculo") String codTipo,
                                                            @PathParam("codUnidade") Long codUnidade,
                                                            @QueryParam("limit") int limit,
                                                            @QueryParam("offset") long offset,
                                                            @PathParam("status") String status){
        return service.getResumoManutencaoHolder(placa, codTipo, codUnidade, limit, offset, status);
    }
}
