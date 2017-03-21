package br.com.zalf.prolog.webservice.gente.quizRelatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.OutputStream;

/**
 * Created by Zart on 20/03/17.
 */
@Path("/quiz/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.Pneu.VISUALIZAR)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizRelatorioResource {

    private QuizRelatorioService service = new QuizRelatorioService();

    @GET
    @Path("/realizados/{codUnidade}/{codModeloQuiz}/csv")
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
    public Report getEstratificacaoRealizacaoQuizReport(@PathParam("codUnidade") Long codUnidade,
                                                        @PathParam("codModeloQuiz") String codModeloQuiz,
                                                        @QueryParam("dataInicial") long dataInicial,
                                                        @QueryParam("dataFinal") long dataFinal) {
        return service.getEstratificacaoRealizacaoQuizReport(codModeloQuiz, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/cargos/{codUnidade}/{codModeloQuiz}/csv")
    public StreamingOutput getRealizacaoQuizByCargoCsv(@PathParam("codUnidade") Long codUnidade,
                                                       @PathParam("codModeloQuiz") String codModeloQuiz) {
        return outputStream -> service.getRealizacaoQuizByCargoCsv(outputStream, codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/cargos/{codUnidade}/{codModeloQuiz}/report")
    public Report getRealizacaoQuizByCargoReport(@PathParam("codUnidade") Long codUnidade,
                                                 @PathParam("codModeloQuiz") String codModeloQuiz,
                                                 @QueryParam("dataInicial") long dataInicial,
                                                 @QueryParam("dataFinal") long dataFinal) {
        return service.getRealizacaoQuizByCargoReport(codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/respostas/{codUnidade}/{codModeloQuiz}/csv")
    public StreamingOutput getEstratificacaoQuizRespostasCsv(OutputStream out, @PathParam("codUnidade") Long codUnidade,
                                                             @PathParam("codModeloQuiz") String codModeloQuiz) {
        return outputStream -> service.getEstratificacaoQuizRespostasCsv(outputStream, codUnidade, codModeloQuiz);
    }

    @GET
    @Path("/respostas/{codUnidade}/{codModeloQuiz}/report")
    public Report getEstratificacaoQuizRespostasReport(@PathParam("codUnidade") Long codUnidade,
                                                       @PathParam("codModeloQuiz") String codModeloQuiz) {
        return service.getEstratificacaoQuizRespostasReport(codUnidade, codModeloQuiz);
    }
}
