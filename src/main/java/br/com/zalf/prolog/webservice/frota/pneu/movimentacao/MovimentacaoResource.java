package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
@Path("/movimentacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 51,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class MovimentacaoResource {
    private final MovimentacaoService service = new MovimentacaoService();

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE_TO_DESCARTE})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public AbstractResponse insert(@Required final ProcessoMovimentacao movimentacao) {
        return service.insert(movimentacao);
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_DESCARTE)
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/motivos-descarte/{codEmpresa}")
    public AbstractResponse insert(@Required final Motivo motivo,
                                   @PathParam("codEmpresa") @Required final Long codEmpresa) {
        return service.insertMotivo(motivo, codEmpresa);
    }

    @PUT
    @Secured(permissions = Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_DESCARTE)
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/motivos-descarte/{codEmpresa}/{codMotivo}/status")
    public Response updateMotivoStatus(@PathParam("codEmpresa") @Required final Long codEmpresa,
                                       @PathParam("codMotivo") @Required final Long codMotivo,
                                       final Motivo motivo) {
        if (service.updateMotivoStatus(codEmpresa, codMotivo, motivo)) {
            return Response.ok("Motivo atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar motivo");
        }
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE_TO_DESCARTE})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/motivos-descarte/{codEmpresa}")
    public List<Motivo> getMotivosAtivos(@PathParam("codEmpresa") @Required final Long codEmpresa,
                                         @QueryParam("apenasAtivos") @Required final Boolean apenasAtivos) {
        return service.getMotivos(codEmpresa, apenasAtivos);
    }
}