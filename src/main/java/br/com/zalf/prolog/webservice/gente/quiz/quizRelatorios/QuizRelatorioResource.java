package br.com.zalf.prolog.webservice.gente.quiz.quizRelatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 20/03/17.
 */
@Path("/quizzes/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizRelatorioResource {

    private QuizRelatorioService service = new QuizRelatorioService();

    @GET
    @Path("/realizados/{codUnidade}/{codModeloQuiz}/csv")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    @Produces("application/csv")
    public StreamingOutput getEstratificacaoRealizacaoQuizCsv(@PathParam("codUnidade") Long codUnidade,
                                                              @PathParam("codModeloQuiz") String codModeloQuiz,
                                                              @QueryParam("dataInicial") long dataInicial,
                                                              @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getEstratificacaoRealizacaoQuizCsv(outputStream, codModeloQuiz, codUnidade, dataInicial,
                dataFinal);
    }

    @GET
    @Path("/realizados/{codUnidade}/{codModeloQuiz}/report")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    public Report getEstratificacaoRealizacaoQuizReport(@PathParam("codUnidade") Long codUnidade,
                                                        @PathParam("codModeloQuiz") String codModeloQuiz,
                                                        @QueryParam("dataInicial") long dataInicial,
                                                        @QueryParam("dataFinal") long dataFinal) {
        return service.getEstratificacaoRealizacaoQuizReport(codModeloQuiz, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/cargos/{codUnidade}/{codModeloQuiz}/csv")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    @Produces("application/csv")
    public StreamingOutput getRealizacaoQuizByCargoCsv(@PathParam("codUnidade") Long codUnidade,
                                                       @PathParam("codModeloQuiz") String codModeloQuiz) {
        return outputStream -> service.getRealizacaoQuizByCargoCsv(outputStream, codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/cargos/{codUnidade}/{codModeloQuiz}/report")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    public Report getRealizacaoQuizByCargoReport(@PathParam("codUnidade") Long codUnidade,
                                                 @PathParam("codModeloQuiz") String codModeloQuiz,
                                                 @QueryParam("dataInicial") long dataInicial,
                                                 @QueryParam("dataFinal") long dataFinal) {
        return service.getRealizacaoQuizByCargoReport(codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/respostas/{codUnidade}/{codModeloQuiz}/csv")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    @Produces("application/csv")
    public StreamingOutput getEstratificacaoQuizRespostasCsv(@PathParam("codUnidade") Long codUnidade,
                                                             @PathParam("codModeloQuiz") String codModeloQuiz) {
        return outputStream -> service.getEstratificacaoQuizRespostasCsv(outputStream, codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/respostas/{codUnidade}/{codModeloQuiz}/report")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    public Report getEstratificacaoQuizRespostasReport(@PathParam("codUnidade") Long codUnidade,
                                                       @PathParam("codModeloQuiz") String codModeloQuiz) {
        return service.getEstratificacaoQuizRespostasReport(codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/consolidados/{codUnidade}/csv")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    @Produces("application/csv")
    public StreamingOutput getExtratoGeralCsv(@PathParam("codUnidade") Long codUnidade,
                                              @QueryParam("dataInicial") long dataInicial,
                                              @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getExtratoGeralCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/consolidados/{codUnidade}/report")
    @Secured(permissions = Pilares.Gente.Relatorios.QUIZ)
    public Report getExtratoGeralReport(@PathParam("codUnidade") Long codUnidade,
                                        @QueryParam("dataInicial") long dataInicial,
                                        @QueryParam("dataFinal") long dataFinal) {
        return service.getExtratoGeralReport(codUnidade, dataInicial, dataFinal);
    }
}
