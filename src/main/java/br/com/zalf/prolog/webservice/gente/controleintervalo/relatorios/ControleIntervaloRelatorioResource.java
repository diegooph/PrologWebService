package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
@Path("/intervalos/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ControleIntervaloRelatorioResource {

    private ControleIntervaloRelatorioService service = new ControleIntervaloRelatorioService();

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
        return outputStream -> service.getAderenciaIntervalosDiariaCsv(outputStream, codUnidade, dataInicial,
                dataFinal);
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
        return outputStream -> service.getAderenciaIntervalosColaboradorCsv(outputStream, codUnidade, dataInicial,
                dataFinal, cpf);
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

    @GET
    @Secured
    @Path("/intervalos-padrao-portaria-1510/{codUnidade}/{codTipoIntervalo}/{cpf}/csv")
    public StreamingOutput getIntervalosPadraoPortaria1510Csv(@PathParam("codUnidade") @Required Long codUnidade,
                                                              @PathParam("codTipoIntervalo") @Required Long codTipoIntervalo,
                                                              @PathParam("cpf") @Required String cpf,
                                                              @QueryParam("dataInicial") @Required String dataInicial,
                                                              @QueryParam("dataFinal") @Required String dataFinal) {
        return outputStream -> service.getIntervalosPadraoPortaria1510(
                outputStream,
                codUnidade,
                codTipoIntervalo,
                cpf,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Path("/folha-ponto/{codUnidade}/{codTipoIntervalo}/{cpf}")
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@PathParam("codUnidade") @Required Long codUnidade,
                                                            @PathParam("cpf") @Required String cpf,
                                                            @PathParam("codTipoIntervalo") @Required String codTipoIntervalo,
                                                            @QueryParam("dataHoraInicial") @Required String dataHoraInicial,
                                                            @QueryParam("dataHoraFinal") @Required String dataHoraFinal) {
        return service.getFolhaPontoRelatorio(
                codUnidade,
                codTipoIntervalo,
                cpf,
                dataHoraInicial,
                dataHoraFinal);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/intervalos-comparando-escala-diaria/{codUnidade}/{codTipoIntervalo}/csv")
    public StreamingOutput getMarcacoesComparandoEscalaDiariaCsv(@PathParam("codUnidade") @Required Long codUnidade,
                                                                 @PathParam("codTipoIntervalo") @Required Long codTipoIntervalo,
                                                                 @QueryParam("dataInicial") @Required String dataInicial,
                                                                 @QueryParam("dataFinal") @Required String dataFinal) {

        return outputStream -> service.getMarcacoesComparandoEscalaDiariaCsv(
                outputStream,
                codUnidade,
                codTipoIntervalo,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Path("/intervalos-comparando-escala-diaria/{codUnidade}/{codTipoIntervalo}/report")
    public Report getMarcacoesComparandoEscalaDiariaReport(@PathParam("codUnidade") @Required Long codUnidade,
                                                           @PathParam("codTipoIntervalo") @Required Long codTipoIntervalo,
                                                           @QueryParam("dataInicial") @Required String dataInicial,
                                                           @QueryParam("dataFinal") @Required String dataFinal) {

        return service.getMarcacoesComparandoEscalaDiariaReport(
                codUnidade,
                codTipoIntervalo,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/total-tempo-por-tipo-intervalo/{codUnidade}/{codTipoIntervalo}/csv")
    public StreamingOutput getTotalTempoByTipoIntervaloCsv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                           @PathParam("codTipoIntervalo") @Required final String codTipoIntervalo,
                                                           @QueryParam("dataInicial") @Required final String dataInicial,
                                                           @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> service.getTotalTempoByTipoIntervaloCsv(
                outputStream,
                codUnidade,
                codTipoIntervalo,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Path("/total-tempo-por-tipo-intervalo/{codUnidade}/{codTipoIntervalo}/report")
    public Report getTotalTempoByTipoIntervaloReport(@PathParam("codUnidade") @Required final Long codUnidade,
                                                     @PathParam("codTipoIntervalo") @Required final String codTipoIntervalo,
                                                     @QueryParam("dataInicial") @Required final String dataInicial,
                                                     @QueryParam("dataFinal") @Required final String dataFinal) {
        return service.getTotalTempoByTipoIntervaloReport(
                codUnidade,
                codTipoIntervalo,
                dataInicial,
                dataFinal);
    }
}