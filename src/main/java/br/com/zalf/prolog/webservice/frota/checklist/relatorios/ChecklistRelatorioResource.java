package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
@Path("/checklists/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
public class ChecklistRelatorioResource {
    private final ChecklistRelatorioService service = new ChecklistRelatorioService();

    @GET
    @Path("/ambev-checklists-realizados-dia/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getChecklistsRealizadosDiaAmbevCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                              @QueryParam("dataInicial") String dataInicial,
                                                              @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getChecklistsRealizadosDiaAmbevCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/ambev-checklists-realizados-dia/report")
    public Report getChecklistsRealizadosDiaAmbevReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                                        @QueryParam("dataInicial") String dataInicial,
                                                        @QueryParam("dataFinal") String dataFinal) {
        return service.getChecklistsRealizadosDiaAmbevReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/ambev-extrato-checklists-realizados-dia/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getExtratoChecklistsRealizadosDiaAmbevCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                                     @QueryParam("dataInicial") String dataInicial,
                                                                     @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getExtratoChecklistsRealizadosDiaAmbevCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/ambev-extrato-checklists-realizados-dia/report")
    public Report getExtratoChecklistsRealizadosDiaAmbevReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                                               @QueryParam("dataInicial") String dataInicial,
                                                               @QueryParam("dataFinal") String dataFinal) {
        return service.getExtratoChecklistsRealizadosDiaAmbevReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/tempo-realizacao-checklists-motorista/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getTempoRealizacaoChecklistsMotoristaCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                                    @QueryParam("dataInicial") String dataInicial,
                                                                    @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getTempoRealizacaoChecklistsMotoristaCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/tempo-realizacao-checklists-motorista/report")
    public Report getTempoRealizacaoChecklistsMotoristaReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                                              @QueryParam("dataInicial") String dataInicial,
                                                              @QueryParam("dataFinal") String dataFinal) {
        return service.getTempoRealizacaoChecklistsMotoristaReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/resumo-checklists/{placa}/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getResumoChecklistsCsv(@PathParam("placa") String placa,
                                                  @QueryParam("codUnidades") List<Long> codUnidades,
                                                  @QueryParam("dataInicial") String dataInicial,
                                                  @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getResumoChecklistsCsv(outputStream, codUnidades, placa, dataInicial, dataFinal);
    }

    @GET
    @Path("/resumo-checklists/{placa}/report")
    public Report getResumoChecklistsReport(@PathParam("placa") String placa,
                                            @QueryParam("codUnidades") List<Long> codUnidades,
                                            @QueryParam("dataInicial") String dataInicial,
                                            @QueryParam("dataFinal") String dataFinal) {
        return service.getResumoChecklistsReport(codUnidades, placa, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-respostas-nok/{placa}/report")
    public Report getEstratificacaoRespostasNokReport(@PathParam("placa") String placa,
                                                      @QueryParam("codUnidades") List<Long> codUnidades,
                                                      @QueryParam("dataInicial") String dataInicial,
                                                      @QueryParam("dataFinal") String dataFinal) {
        return service.getEstratificacaoRespostasNokReport(codUnidades, placa, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-respostas-nok/{placa}/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getEstratificacaoRespostasNokCsv(@PathParam("placa") String placa,
                                                            @QueryParam("codUnidades") List<Long> codUnidades,
                                                            @QueryParam("dataInicial") String dataInicial,
                                                            @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getEstratificacaoRespostasNokCsv(outputStream, codUnidades, placa, dataInicial, dataFinal);
    }
}