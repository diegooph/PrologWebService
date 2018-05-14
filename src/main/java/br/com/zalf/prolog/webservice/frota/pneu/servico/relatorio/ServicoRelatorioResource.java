package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

@Path("/pneus/servicos/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 51,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public final class ServicoRelatorioResource {
    private final ServicoRelatorioService service = new ServicoRelatorioService();

    @GET
    @Path("/estratificacao-servicos-fechados/{codUnidade}/report")
    public Report getEstratificacaoServicosFechadosReport(@PathParam("codUnidade") @Required final Long codUnidade,
                                                          @QueryParam("dataInicial") @Required final String dataInicial,
                                                          @QueryParam("dataFinal") @Required final String dataFinal) {
        return service.getEstratificacaoServicosFechadosReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-servicos-fechados/{codUnidade}/csv")
    public StreamingOutput getEstratificacaoServicosFechadosCsv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                                @QueryParam("dataInicial") @Required final String dataInicial,
                                                                @QueryParam("dataFinal") @Required final String dataFinal)
            throws RuntimeException {
        return outputStream -> service.getEstratificacaoServicosFechadosCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-servicos-abertos/{codUnidade}/report")
    public Report getEstratificacaoServicosAbertosReport(@PathParam("codUnidade") @Required final Long codUnidade,
                                                         @QueryParam("dataInicial") @Required final String dataInicial,
                                                         @QueryParam("dataFinal") @Required final String dataFinal) {
        return service.getEstratificacaoServicosAbertosReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-servicos-abertos/{codUnidade}/csv")
    public StreamingOutput getEstratificacaoServicosAbertosCsv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                               @QueryParam("dataInicial") @Required final String dataInicial,
                                                               @QueryParam("dataFinal") @Required final String dataFinal)
            throws RuntimeException {
        return outputStream -> service.getEstratificacaoServicosAbertosCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }
}