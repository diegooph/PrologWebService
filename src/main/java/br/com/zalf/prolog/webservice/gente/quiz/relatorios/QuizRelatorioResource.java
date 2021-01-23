package br.com.zalf.prolog.webservice.gente.quiz.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 20/03/17.
 */
@Path("/quizzes/relatorios")
@Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 76,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class QuizRelatorioResource {
    @NotNull
    private final QuizRelatorioService service = new QuizRelatorioService();

    @GET
    @Path("/estratificacao-realizados/csv")
    @Produces("application/csv")
    public StreamingOutput getEstratificacaoRealizacaoQuizCsv(@QueryParam("codUnidade") @Required Long codUnidade,
                                                              @QueryParam("codModeloQuiz") @Optional Long codModeloQuiz,
                                                              @QueryParam("dataInicial") @Required String dataInicial,
                                                              @QueryParam("dataFinal") @Required String dataFinal) {
        return outputStream -> service.getEstratificacaoRealizacaoQuizCsv(
                outputStream,
                codUnidade,
                codModeloQuiz,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/estratificacao-realizados/report")
    public Report getEstratificacaoRealizacaoQuizReport(
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("codModeloQuiz") @Optional Long codModeloQuiz,
            @QueryParam("dataInicial") @Required String dataInicial,
            @QueryParam("dataFinal") @Required String dataFinal) throws ProLogException {
        return service.getEstratificacaoRealizacaoQuizReport(
                codUnidade,
                codModeloQuiz,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/realizacoes-por-cargos/csv")
    @Produces("application/csv")
    public StreamingOutput getRealizacaoQuizByCargoCsv(@QueryParam("codUnidade") @Required Long codUnidade,
                                                       @QueryParam("codModeloQuiz") @Optional Long codModeloQuiz) {
        return outputStream -> service.getRealizacaoQuizByCargoCsv(
                outputStream,
                codUnidade,
                codModeloQuiz);
    }

    @GET
    @Path("/realizacoes-por-cargos/report")
    public Report getRealizacaoQuizByCargoReport(
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("codModeloQuiz") Long codModeloQuiz) throws ProLogException {
        return service.getRealizacaoQuizByCargoReport(
                codUnidade,
                codModeloQuiz);
    }

    @GET
    @Path("/estratificacao-respostas/csv")
    @Produces("application/csv")
    public StreamingOutput getEstratificacaoQuizRespostasCsv(@QueryParam("codUnidade") @Required Long codUnidade,
                                                             @QueryParam("codModeloQuiz") @Optional Long codModeloQuiz) {
        return outputStream -> service.getEstratificacaoQuizRespostasCsv(
                outputStream,
                codUnidade,
                codModeloQuiz);
    }

    @GET
    @Path("/estratificacao-respostas/report")
    public Report getEstratificacaoQuizRespostasReport(@QueryParam("codUnidade") @Required Long codUnidade,
                                                       @QueryParam("codModeloQuiz") @Optional Long codModeloQuiz)
            throws ProLogException {
        return service.getEstratificacaoQuizRespostasReport(
                codUnidade,
                codModeloQuiz);
    }

    @GET
    @Path("/extrato-geral/csv")
    @Produces("application/csv")
    public StreamingOutput getExtratoGeralCsv(@QueryParam("codUnidade") @Required Long codUnidade,
                                              @QueryParam("dataInicial") @Required String dataInicial,
                                              @QueryParam("dataFinal") @Required String dataFinal) {
        return outputStream -> service.getExtratoGeralCsv(
                outputStream,
                codUnidade,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/extrato-geral/report")
    public Report getExtratoGeralReport(@QueryParam("codUnidade") @Required Long codUnidade,
                                        @QueryParam("dataInicial") @Required String dataInicial,
                                        @QueryParam("dataFinal") @Required String dataFinal) throws ProLogException {
        return service.getExtratoGeralReport(
                codUnidade,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/respostas-realizados/csv")
    @Produces("application/csv")
    public StreamingOutput getRespostasRealizadosCsv(
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("codModeloQuiz") @Optional Long codModelo,
            @QueryParam("cpfColaborador") @Optional Long cpfColaborador,
            @QueryParam("dataInicial") @Required String dataInicial,
            @QueryParam("dataFinal") @Required String dataFinal,
            @QueryParam("apenasRespostasSelecionadas") @Required boolean apenasRespostasSelecionadas) {
        return outputStream -> service.getRespostasRealizadosCsv(
                outputStream,
                codUnidade,
                codModelo,
                cpfColaborador,
                dataInicial,
                dataFinal,
                apenasRespostasSelecionadas);
    }

    @GET
    @Path("/respostas-realizados/report")
    public Report getRespostasRealizadosReport(
            @QueryParam("codUnidade") @Required Long codUnidade,
            @QueryParam("codModeloQuiz") @Optional Long codModelo,
            @QueryParam("cpfColaborador") @Optional Long cpfColaborador,
            @QueryParam("dataInicial") @Required String dataInicial,
            @QueryParam("dataFinal") @Required String dataFinal,
            @QueryParam("apenasRespostasSelecionadas") @Required boolean apenasRespostasSelecionadas)
            throws ProLogException {
        return service.getRespostasRealizadosReport(
                codUnidade,
                codModelo,
                cpfColaborador,
                dataInicial,
                dataFinal,
                apenasRespostasSelecionadas);
    }
}
