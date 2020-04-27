package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by Zart on 28/08/2017.
 */
@DebugLog
@Path("/intervalos/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ControleJornadaRelatorioResource {

    private final ControleJornadaRelatorioService service = new ControleJornadaRelatorioService();

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/realizados/{codUnidade}/{cpf}/csv")
    public StreamingOutput getMarcacoesDiariasCsv(@PathParam("codUnidade") final Long codUnidade,
                                                  @QueryParam("dataInicial") final Long dataInicial,
                                                  @QueryParam("dataFinal") final Long dataFinal,
                                                  @PathParam("cpf") final String cpf) {
        return outputStream -> service.getMarcacoesDiariasCsv(outputStream, codUnidade, dataInicial, dataFinal, cpf);
    }

    @GET
    @Secured
    @Path("/realizados/{codUnidade}/{cpf}/report")
    public Report getMarcacoesDiariasReport(@PathParam("codUnidade") final Long codUnidade,
                                            @QueryParam("dataInicial") final Long dataInicial,
                                            @QueryParam("dataFinal") final Long dataFinal,
                                            @PathParam("cpf") final String cpf) {
        return service.getMarcacoesDiariasReport(codUnidade, dataInicial, dataFinal, cpf);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/realizados/mapas/{codUnidade}/csv")
    public StreamingOutput getIntervalosMapasCsv(@PathParam("codUnidade") final Long codUnidade,
                                                 @QueryParam("dataInicial") final Long dataInicial,
                                                 @QueryParam("dataFinal") final Long dataFinal) {
        return outputStream -> service.getIntervalosMapasCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/realizados/mapas/{codUnidade}/report")
    public Report getIntervalosMapasReport(@PathParam("codUnidade") final Long codUnidade,
                                           @QueryParam("dataInicial") final Long dataInicial,
                                           @QueryParam("dataFinal") final Long dataFinal) {
        return service.getIntervalosMapasReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/aderencias/diarias/{codUnidade}/csv")
    public StreamingOutput getAderenciaIntervalosDiariaCsv(@PathParam("codUnidade") final Long codUnidade,
                                                           @QueryParam("dataInicial") final Long dataInicial,
                                                           @QueryParam("dataFinal") final Long dataFinal) {
        return outputStream -> service.getAderenciaIntervalosDiariaCsv(outputStream, codUnidade, dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Path("/aderencias/diarias/{codUnidade}/report")
    public Report getAderenciaIntervalosDiariaReport(@PathParam("codUnidade") final Long codUnidade,
                                                     @QueryParam("dataInicial") final Long dataInicial,
                                                     @QueryParam("dataFinal") final Long dataFinal) {
        return service.getAderenciaIntervalosDiariaReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/aderencia-marcacoes-colaboradores-mapa/csv")
    public StreamingOutput getAderenciaMarcacoesColaboradoresCsv(@QueryParam("codUnidade") @Required final Long codUnidade,
                                                                 @QueryParam("cpf") @Optional final Long cpf,
                                                                 @QueryParam("dataInicial") @Required final String dataInicial,
                                                                 @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> service.getAderenciaMarcacoesColaboradoresCsv(
                outputStream,
                codUnidade,
                cpf,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Path("/aderencia-marcacoes-colaboradores-mapa/report")
    public Report getAderenciaMarcacoesColaboradoresReport(@QueryParam("codUnidade") @Required final Long codUnidade,
                                                           @QueryParam("cpf") @Optional final Long cpf,
                                                           @QueryParam("dataInicial") @Required final String dataInicial,
                                                           @QueryParam("dataFinal") @Required final String dataFinal) {
        return service.getAderenciaMarcacoesColaboradoresReport(codUnidade, cpf, dataInicial, dataFinal);
    }

    @GET
    @Secured
    @Path("/intervalos-padrao-portaria-1510/{codUnidade}/{codTipoIntervalo}/{cpf}/csv")
    public StreamingOutput getIntervalosPadraoPortaria1510Csv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                              @PathParam("codTipoIntervalo") @Required final Long codTipoIntervalo,
                                                              @PathParam("cpf") @Required final String cpf,
                                                              @QueryParam("dataInicial") @Required final String dataInicial,
                                                              @QueryParam("dataFinal") @Required final String dataFinal) {
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
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("cpf") @Required final String cpf,
            @PathParam("codTipoIntervalo") @Required final String codTipoIntervalo,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("apenasColaboradoresAtivos") @Optional final boolean apenasColaboradoresAtivos) throws Throwable {
        return service.getFolhaPontoRelatorio(
                codUnidade,
                codTipoIntervalo,
                cpf,
                dataInicial,
                dataFinal,
                apenasColaboradoresAtivos);
    }

    @GET
    @Secured
    @Path("/folha-ponto-jornada/{codUnidade}/{codTipoIntervalo}/{cpf}")
    public List<FolhaPontoJornadaRelatorio> getFolhaPontoJornadaRelatorio(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("cpf") @Required final String cpf,
            @PathParam("codTipoIntervalo") @Required final String codTipoIntervalo,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @QueryParam("apenasColaboradoresAtivos") @Optional final boolean apenasColaboradoresAtivos) throws Throwable {
        return service.getFolhaPontoJornadaRelatorio(
                codUnidade,
                codTipoIntervalo,
                cpf,
                dataInicial,
                dataFinal,
                apenasColaboradoresAtivos);
    }

    @GET
    @Secured
    @Produces("application/csv")
    @Path("/intervalos-comparando-escala-diaria/{codUnidade}/{codTipoIntervalo}/csv")
    public StreamingOutput getMarcacoesComparandoEscalaDiariaCsv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                                 @PathParam("codTipoIntervalo") @Required final Long codTipoIntervalo,
                                                                 @QueryParam("dataInicial") @Required final String dataInicial,
                                                                 @QueryParam("dataFinal") @Required final String dataFinal) {

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
    public Report getMarcacoesComparandoEscalaDiariaReport(@PathParam("codUnidade") @Required final Long codUnidade,
                                                           @PathParam("codTipoIntervalo") @Required final Long codTipoIntervalo,
                                                           @QueryParam("dataInicial") @Required final String dataInicial,
                                                           @QueryParam("dataFinal") @Required final String dataFinal) {

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
    public Report getTotalTempoByTipoIntervaloReport(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codTipoIntervalo") @Required final String codTipoIntervalo,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws Throwable {
        return service.getTotalTempoByTipoIntervaloReport(
                codUnidade,
                codTipoIntervalo,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured
    @Path("/exportacao-marcacoes/csv")
    public StreamingOutput getMarcacoesExportacaoGenericaCsv(@QueryParam("codUnidade") @Required final Long codUnidade,
                                                             @QueryParam("codTipoIntervalo") @Optional final Long codTipoIntervalo,
                                                             @QueryParam("codColaborador") @Optional final Long codColaborador,
                                                             @QueryParam("apenasMarcacoesAtivas") @Optional final boolean apenasMarcacoesAtivas,
                                                             @QueryParam("dataInicial") @Required final String dataInicial,
                                                             @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> service.getMarcacoesExportacaoGenericaCsv(
                outputStream,
                codUnidade,
                codTipoIntervalo,
                codColaborador,
                apenasMarcacoesAtivas,
                dataInicial,
                dataFinal);
    }
}