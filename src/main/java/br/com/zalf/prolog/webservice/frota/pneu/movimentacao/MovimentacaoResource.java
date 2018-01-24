package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
@Path("/movimentacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MovimentacaoResource {
    private final MovimentacaoService service = new MovimentacaoService();

    @Secured
    @POST
    public AbstractResponse insert(ProcessoMovimentacao movimentacao){
        return service.insert(movimentacao);
    }

    @POST
    @Secured
    @Path("/motivos/descarte/{codEmpresa}")
    public AbstractResponse insert(Motivo motivo,
                           @PathParam("codEmpresa") Long codEmpresa) {
        return service.insertMotivo(motivo, codEmpresa);
    }

    @GET
    @Secured
    @Path("/motivos/descarte/{codEmpresa}")
    public List<Motivo> getMotivosAtivos(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMotivos(codEmpresa, true);
    }

    @GET
    @Secured
    @Path("/motivos/descarte/todos/{codEmpresa}")
    public List<Motivo> getTodosMotivos(@PathParam("codEmpresa") Long codEmpresa) {
        return service.getMotivos(codEmpresa, false);
    }

    @PUT
    @Secured
    @Path("/motivos/descarte/{codEmpresa}/{codMotivo}/status")
    public Response updateMotivoStatus(@PathParam("codEmpresa") Long codEmpresa,
                                 @PathParam("codMotivo") Long codMotivo,
                                 final Motivo motivo) {
        if (service.updateMotivoStatus(codEmpresa, codMotivo, motivo)) {
            return Response.ok("Motivo atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar motivo");
        }
    }
}
