package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
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
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/motivos-descarte/{codEmpresa}")
    public AbstractResponse insert(@Required Motivo motivo, @PathParam("codEmpresa") @Required Long codEmpresa) {
        return service.insertMotivo(motivo, codEmpresa);
    }

    @GET
    @Secured
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/motivos-descarte/{codEmpresa}")
    public List<Motivo> getMotivosAtivos(@PathParam("codEmpresa") @Required Long codEmpresa,
                                         @QueryParam("apenasAtivos") @Required Boolean apenasAtivos) {
        return service.getMotivos(codEmpresa, apenasAtivos);
    }

    @PUT
    @Secured
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/motivos-descarte/{codEmpresa}/{codMotivo}/status")
    public Response updateMotivoStatus(@PathParam("codEmpresa") @Required Long codEmpresa,
                                       @PathParam("codMotivo") @Required Long codMotivo,
                                       final Motivo motivo) {
        if (service.updateMotivoStatus(codEmpresa, codMotivo, motivo)) {
            return Response.ok("Motivo atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar motivo");
        }
    }
}
