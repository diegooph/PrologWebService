package br.com.zalf.prolog.webservice.gente.quizRelatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 20/03/17.
 */
@Path("/quiz/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.Pneu.VISUALIZAR)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class QuizRelatorioResource {

    QuizRelatorioService service = new QuizRelatorioService();

    @GET
    @Path("/realizacao/{codUnidade}/{codModeloQuiz}/{cpf}/csv")
    @Produces("application/csv")
    public StreamingOutput getEstratificacaoRealizacaoQuizCsv(@PathParam("codUnidade") Long codUnidade,
                                                              @PathParam("codModeloQuiz") String codModeloQuiz,
                                                              @PathParam("cpf") String cpf,
                                                              @QueryParam("dataInicial") long dataInicial,
                                                              @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getEstratificacaoRealizacaoQuizCsv(outputStream, cpf, codModeloQuiz, codUnidade, dataInicial,
                dataFinal);
    }

    @GET
    @Path("/realizacao/{codUnidade}/{codModeloQuiz}/{cpf}/report")
    public Report getEstratificacaoRealizacaoQuizReport(@PathParam("codUnidade") Long codUnidade,
                                                        @PathParam("codModeloQuiz") String codModeloQuiz,
                                                        @PathParam("cpf") String cpf,
                                                        @QueryParam("dataInicial") long dataInicial,
                                                        @QueryParam("dataFinal") long dataFinal) {
        return service.getEstratificacaoRealizacaoQuizReport(cpf, codModeloQuiz, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/cargos/{codUnidade}/{codCargo}/{codModeloQuiz}/csv")
    public StreamingOutput getRealizacaoQuizByCargoCsv(@PathParam("codUnidade") Long codUnidade,
                                            @PathParam("codCargo") String codCargo,
                                            @PathParam("codModeloQuiz") String codModeloQuiz) {
        return outputStream -> service.getRealizacaoQuizByCargoCsv(outputStream, codUnidade, codCargo, codModeloQuiz);
    }

    @GET
    @Path("/cargos/{codUnidade}/{codCargo}/{codModeloQuiz}/report")
    public Report getRealizacaoQuizByCargoReport (@PathParam("codUnidade") Long codUnidade,
                                               @PathParam("codCargo") String codCargo,
                                               @PathParam("codModeloQuiz") String codModeloQuiz) {
        return service.getRealizacaoQuizByCargoReport(codUnidade, codCargo, codModeloQuiz);
    }

}
