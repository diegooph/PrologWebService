package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by luiz on 25/04/17.
 */
@Path("/checklists/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
public class ChecklistRelatorioResource {
    private ChecklistRelatorioService service = new ChecklistRelatorioService();

    @GET
    @Path("/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getCheckilistsRealizadosDiaCsv(@PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getCheckilistsRealizadosDiaCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/{codUnidade}/report")
    public Report getCheckilistsRealizadosDiaReport(@PathParam("codUnidade") Long codUnidade,
                                                    @QueryParam("dataInicial") long dataInicial,
                                                    @QueryParam("dataFinal") long dataFinal) {
        return service.getChecklistsRealizadosDiaReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/extrato/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getExtratoChecklistsRealizadosDiaCsv(@PathParam("codUnidade") Long codUnidade,
                                                                @QueryParam("dataInicial") long dataInicial,
                                                                @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getExtratoChecklistsRealizadosDiaCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/extrato/{codUnidade}/report")
    public Report getExtratoChecklistsRealizadosDiaReport(@PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) {
        return service.getExtratoChecklistsRealizadosDiaReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/tempos-motoristas/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getTempoRealizacaoChecklistMotoristaCsv(@PathParam("codUnidade") Long codUnidade,
                                                                   @QueryParam("dataInicial") long dataInicial,
                                                                   @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getTempoRealizacaoChecklistMotoristaCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/tempos-motoristas/{codUnidade}/report")
    public Report getTempoRealizacaoChecklistMotoristaReport(@PathParam("codUnidade") Long codUnidade,
                                                             @QueryParam("dataInicial") long dataInicial,
                                                             @QueryParam("dataFinal") long dataFinal) {
        return service.getTempoRealizacaoChecklistMotoristaReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/resumos/{codUnidade}/{placa}/csv")
    public StreamingOutput getResumoChecklistCsv(@PathParam("codUnidade") Long codUnidade,
                                                 @QueryParam("dataInicial") Long dataInicial,
                                                 @QueryParam("dataFinal") Long dataFinal,
                                                 @PathParam("placa") String placa) {
        return outputStream -> service.getResumoChecklistCsv(outputStream, codUnidade, dataInicial, dataFinal, placa);
    }

    @GET
    @Path("/resumos/{codUnidade}/{placa}/report")
    public Report getResumoChecklistReport(@PathParam("codUnidade") Long codUnidade,
                                           @QueryParam("dataInicial") Long dataInicial,
                                           @QueryParam("dataFinal") Long dataFinal,
                                           @PathParam("placa") String placa) {
        return service.getResumoChecklistReport(codUnidade, dataInicial, dataFinal, placa);
    }

    @GET
    @Path("/estratificacoes/{codUnidade}/{placa}/{statusOs}/{statusItem}/report")
    public Report getEstratificacaoRespostasNokReport(@PathParam("codUnidade") Long codUnidade,
                                                      @PathParam("placa") String placa,
                                                      @QueryParam("dataInicial") Long dataInicial,
                                                      @QueryParam("dataFinal") Long dataFinal) {
        return service.getEstratificacaoRespostasNokChecklistReport(codUnidade, placa, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacoes/{codUnidade}/{placa}/{statusOs}/{statusItem}/csv")
    public StreamingOutput getEstratificacaoRespostasNokCsv(@PathParam("codUnidade") Long codUnidade,
                                                            @PathParam("placa") String placa,
                                                            @QueryParam("dataInicial") Long dataInicial,
                                                            @QueryParam("dataFinal") Long dataFinal) {
        return outputStream -> service.getEstratificacaoRespostasNokChecklistCsv(outputStream, codUnidade, placa, dataInicial, dataFinal);
    }
}