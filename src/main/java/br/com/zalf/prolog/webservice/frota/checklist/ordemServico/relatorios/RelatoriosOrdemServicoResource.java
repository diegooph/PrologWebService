package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by luiz on 26/04/17.
 */
@Path("/checklists/ordens-servico/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
public class RelatoriosOrdemServicoResource {
    private RelatoriosOrdemServicoService service = new RelatoriosOrdemServicoService();

    @GET
    @Path("/itens/maior-quantidade-nok/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getItensMaiorQuantidadeNokCsv(@PathParam("codUnidade") Long codUnidade,
                                                         @QueryParam("dataInicial") long dataInicial,
                                                         @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getItensMaiorQuantidadeNokCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/itens/maior-quantidade-nok/{codUnidade}/report")
    public Report getItensMaiorQuantidadeNokReport(@PathParam("codUnidade") Long codUnidade,
                                                   @QueryParam("dataInicial") long dataInicial,
                                                   @QueryParam("dataFinal") long dataFinal) {
        return service.getItensMaiorQuantidadeNokReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/itens/media-tempo-conserto/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getMediaTempoConsertoItemCsv(@PathParam("codUnidade") Long codUnidade,
                                                        @QueryParam("dataInicial") long dataInicial,
                                                        @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getMediaTempoConsertoItemCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/itens/media-tempo-conserto/{codUnidade}/report")
    public Report getMediaTempoConsertoItemReport(@PathParam("codUnidade") Long codUnidade,
                                                  @QueryParam("dataInicial") long dataInicial,
                                                  @QueryParam("dataFinal") long dataFinal) {
        return service.getMediaTempoConsertoItemReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/produtividade-mecanicos/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getProdutividadeMecanicosCsv(@PathParam("codUnidade") Long codUnidade,
                                                        @QueryParam("dataInicial") long dataInicial,
                                                        @QueryParam("dataFinal") long dataFinal) {
        return outputStream -> service.getProdutividadeMecanicosCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/produtividade-mecanicos/{codUnidade}/report")
    public Report getProdutividadeMecanicosReport(@PathParam("codUnidade") Long codUnidade,
                                                  @QueryParam("dataInicial") long dataInicial,
                                                  @QueryParam("dataFinal") long dataFinal) {
        return service.getProdutividadeMecanicosReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacoes/{codUnidade}/{placa}/{statusOs}/{statusItem}/report")
    public Report getEstratificacaoOsReport(@PathParam("codUnidade") Long codUnidade,
                                            @PathParam("placa") String placa,
                                            @QueryParam("dataInicial") Long dataInicial,
                                            @QueryParam("dataFinal") Long dataFinal,
                                            @PathParam("statusOs") String statusOs,
                                            @PathParam("statusItem") String statusItem) {
        return service.getEstratificacaoOsReport(codUnidade, placa, dataInicial, dataFinal, statusOs, statusItem);
    }

    @GET
    @Path("/estratificacoes/{codUnidade}/{placa}/{statusOs}/{statusItem}/csv")
    public StreamingOutput getEstratificacaoOsCsv(@PathParam("codUnidade") Long codUnidade,
                                            @PathParam("placa") String placa,
                                            @QueryParam("dataInicial") Long dataInicial,
                                            @QueryParam("dataFinal") Long dataFinal,
                                            @PathParam("statusOs") String statusOs,
                                            @PathParam("statusItem") String statusItem) {
        return outputStream -> service.getEstratificacaoOsCsv(outputStream, codUnidade, placa, dataInicial, dataFinal,
                statusOs, statusItem);
    }


}