package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by Zart on 28/08/2017.
 */
@Path("/intervalos/relatorio")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ControleIntervaloRelatorioResource {

    private ControleIntervalosRelatorioService service = new ControleIntervalosRelatorioService();

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/realizados/{codUnidade}/{cpf}/csv")
    public StreamingOutput getIntervalosCsv(@PathParam("codUnidade") Long codUnidade,
                                            @QueryParam("dataInicial") Long dataInicial,
                                            @QueryParam("dataFinal") Long dataFinal,
                                            @PathParam("cpf") String cpf) {
        return outputStream -> service.getIntervalosCsv(outputStream, codUnidade, dataInicial, dataFinal, cpf);
    }

    @GET
    @Secured
    @Path("/realizados/{codUnidade}/{cpf}/report")
    public Report getIntervalosReport(@PathParam("codUnidade") Long codUnidade,
                                      @QueryParam("dataInicial") Long dataInicial,
                                      @QueryParam("dataFinal") Long dataFinal,
                                      @PathParam("cpf") String cpf) {
        return service.getIntervalosReport(codUnidade, dataInicial, dataFinal, cpf);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/realizados/mapas/{codUnidade}/csv")
    public StreamingOutput getIntervalosMapasCsv(@PathParam("codUnidade") Long codUnidade,
                                                 @QueryParam("dataInicial") Long dataInicial,
                                                 @QueryParam("dataFinal") Long dataFinal) {
        return outputStream -> service.getIntervalosMapasCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/realizados/mapas/{codUnidade}/report")
    public Report getIntervalosMapasReport(@PathParam("codUnidade") Long codUnidade,
                                           @QueryParam("dataInicial") Long dataInicial,
                                           @QueryParam("dataFinal") Long dataFinal) {
        return service.getIntervalosMapasReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/aderencias/diarias/{codUnidade}/csv")
    public StreamingOutput getAderenciaIntervalosDiariaCsv(@PathParam("codUnidade") Long codUnidade,
                                                           @QueryParam("dataInicial") Long dataInicial,
                                                           @QueryParam("dataFinal") Long dataFinal) {
        return outputStream -> service.getAderenciaIntervalosDiariaCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/aderencias/diarias/{codUnidade}/report")
    public Report getAderenciaIntervalosDiariaReport(@PathParam("codUnidade") Long codUnidade,
                                                     @QueryParam("dataInicial") Long dataInicial,
                                                     @QueryParam("dataFinal") Long dataFinal) {
        return service.getAderenciaIntervalosDiariaReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/aderencias/colaboradores/{codUnidade}/{cpf}/csv")
    public StreamingOutput getAderenciaIntervalosColaboradorCsv(@PathParam("codUnidade") Long codUnidade,
                                                                @QueryParam("dataInicial") Long dataInicial,
                                                                @QueryParam("dataFinal") Long dataFinal,
                                                                @PathParam("cpf") String cpf) {
        return outputStream -> service.getAderenciaIntervalosColaboradorCsv(outputStream, codUnidade, dataInicial, dataFinal, cpf);
    }

    @GET
    @Secured
    @Path("/aderencias/colaboradores/{codUnidade}/{cpf}/report")
    public Report getAderenciaIntervalosColaboradorReport(@PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") Long dataInicial,
                                                          @QueryParam("dataFinal") Long dataFinal,
                                                          @PathParam("cpf") String cpf) {
        return service.getAderenciaIntervalosColaboradorReport(codUnidade, dataInicial, dataFinal, cpf);
    }
}
