package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.Response;
import br.com.zalf.prolog.models.checklist.os.ItemOrdemServico;
import br.com.zalf.prolog.models.checklist.os.ManutencaoHolder;
import br.com.zalf.prolog.models.checklist.os.OsHolder;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 */

@Path("/OrdemServico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OrdemServicoResource {

    private OrdemServicoService service = new OrdemServicoService();

    @GET
    @Secured
    @Path("/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    public List<OsHolder> getOs(@PathParam("placa") String placa,
                                @PathParam("status") String status,
                                @PathParam("codUnidade") Long codUnidade,
                                @PathParam("tipoVeiculo") String tipoVeiculo,
                                @QueryParam("limit") Integer limit,
                                @QueryParam("offset") Long offset){

        return service.getOs(placa, status, null, codUnidade, tipoVeiculo, limit, offset);
    }

    @GET
    @Secured
    @Path("/manutencao/{codUnidade}/{status}")
    public List<ManutencaoHolder> getManutencaoHolder (@PathParam("codUnidade") Long codUnidade,
                                                       @QueryParam("limit") int limit,
                                                       @QueryParam("offset") long offset,
                                                       @PathParam("status") String status){
        return service.getManutencaoHolder(codUnidade, limit, offset, status);
    }

    @POST
    @Secured
    @Path("/conserta/{codUnidade}")
    public Response consertaItem (@PathParam("codUnidade")Long codUnidade, ItemOrdemServico item){
        if (service.consertaItem(codUnidade, item)){
            return Response.Ok("Serviço consertado com sucesso");
        }else{
            return Response.Error("Erro ao consertar o item");
        }


    }



}
