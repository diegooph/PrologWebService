package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.relatorios.MovimentacaoRelatorioService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by Zart on 03/03/17.
 */
@DebugLog
@Path("/movimentacoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 57,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class MovimentacaoResource {
    private final MovimentacaoService service = new MovimentacaoService();

    @NotNull
        private final MovimentacaoRelatorioService relatorioService = new MovimentacaoRelatorioService();

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE_TO_DESCARTE})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public AbstractResponse insert(@Required final ProcessoMovimentacao movimentacao) throws ProLogException {
        return service.insert(movimentacao);
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_DESCARTE)
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/motivos-descarte/{codEmpresa}")
    public AbstractResponse insert(@Required final Motivo motivo,
                                   @PathParam("codEmpresa") @Required final Long codEmpresa) throws ProLogException {
        return service.insertMotivo(motivo, codEmpresa);
    }

    @PUT
    @Secured(permissions = Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_DESCARTE)
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/motivos-descarte/{codEmpresa}/{codMotivo}/status")
    public Response updateMotivoStatus(@PathParam("codEmpresa") @Required final Long codEmpresa,
                                       @PathParam("codMotivo") @Required final Long codMotivo,
                                       final Motivo motivo) throws ProLogException {
        service.updateMotivoStatus(codEmpresa, codMotivo, motivo);
        return Response.ok("Motivo atualizado com sucesso");
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_GERAL,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE_TO_DESCARTE})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/motivos-descarte/{codEmpresa}")
    public List<Motivo> getMotivosAtivos(@PathParam("codEmpresa") @Required final Long codEmpresa,
                                         @QueryParam("apenasAtivos") @Required final Boolean apenasAtivos)
            throws ProLogException {
        return service.getMotivos(codEmpresa, apenasAtivos);
    }

    @GET
    @Produces("application/csv")
    @Path("/unidades/{codUnidades}/csv")
    public StreamingOutput getDadosGeraisMovimentacao(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> relatorioService.getDadosGeraisAfericaoCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/unidades/{codUnidades}/report")
    public Report getDadosGeraisMovimentacaoReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                    @QueryParam("dataInicial") @Required final String dataInicial,
                                                    @QueryParam("dataFinal") @Required final String dataFinal)
            throws ProLogException {
        return relatorioService.getDadosGeraisAfericaoReport(codUnidades, dataInicial, dataFinal);
    }
}