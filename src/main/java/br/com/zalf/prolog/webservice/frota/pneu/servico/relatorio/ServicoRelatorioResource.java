package br.com.zalf.prolog.webservice.frota.pneu.servico.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

@Path("/v2/pneus/servicos/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 55,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public final class ServicoRelatorioResource {
    private final ServicoRelatorioService service = new ServicoRelatorioService();

    @GET
    @Path("/estratificacao-servicos-fechados/report")
    public Report getEstratificacaoServicosFechadosReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return service.getEstratificacaoServicosFechadosReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-servicos-fechados/csv")
    public StreamingOutput getEstratificacaoServicosFechadosCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws RuntimeException {
        return outputStream -> service.getEstratificacaoServicosFechadosCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-servicos-abertos/report")
    public Report getEstratificacaoServicosAbertosReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return service.getEstratificacaoServicosAbertosReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-servicos-abertos/csv")
    public StreamingOutput getEstratificacaoServicosAbertosCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws RuntimeException {
        return outputStream -> service.getEstratificacaoServicosAbertosCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    /**
     * @deprecated at 2018-06-21.
     * Use {@link ServicoRelatorioResource#getEstratificacaoServicosFechadosReport(List, String, String)} instead.
     */
    @GET
    @Path("/estratificacao-servicos-fechados/{codUnidade}/report")
    public Report DEPRECATED_GET_ESTRATIFICACAO_SERVICOS_FECHADOS_REPORT(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-21.
     * Use {@link ServicoRelatorioResource#getEstratificacaoServicosFechadosCsv(List, String, String)} instead.
     */
    @GET
    @Path("/estratificacao-servicos-fechados/{codUnidade}/csv")
    public StreamingOutput DEPRECATED_GET_ESTRATIFICACAO_SERVICOS_FECHADOS_CSV(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-21.
     * Use {@link ServicoRelatorioResource#getEstratificacaoServicosAbertosReport(List, String, String)} instead.
     */
    @GET
    @Path("/estratificacao-servicos-abertos/{codUnidade}/report")
    public Report DEPRECATED_GET_ESTRATIFICACAO_SERVICOS_ABERTOS_REPORT(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-21.
     * Use {@link ServicoRelatorioResource#getEstratificacaoServicosAbertosCsv(List, String, String)} instead.
     */
    @GET
    @Path("/estratificacao-servicos-abertos/{codUnidade}/csv")
    public StreamingOutput DEPRECATED_GET_ESTRATIFICACAO_SERVICOS_ABERTOS_CSV(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }
}