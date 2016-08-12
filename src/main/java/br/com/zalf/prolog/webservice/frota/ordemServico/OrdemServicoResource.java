package br.com.zalf.prolog.webservice.frota.ordemServico;

import br.com.zalf.prolog.models.checklist.os.OsHolder;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
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
    public List<OsHolder> getResumoOs(@PathParam("placa") String placa,
                                      @PathParam("status") String status,
                                      @PathParam("codUnidade") Long codUnidade,
                                      @PathParam("tipoVeiculo") String tipoVeiculo,
                                      @QueryParam("limit") Integer limit,
                                      @QueryParam("offset") Long offset){

        return service.getResumoOs(placa, status, null, codUnidade, tipoVeiculo, limit, offset);
    }


}
