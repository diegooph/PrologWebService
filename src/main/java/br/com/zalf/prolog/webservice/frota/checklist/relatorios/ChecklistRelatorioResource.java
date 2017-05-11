package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by luiz on 25/04/17.
 */
@Path("/checklists/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
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
}