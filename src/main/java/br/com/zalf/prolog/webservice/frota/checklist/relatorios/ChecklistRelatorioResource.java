package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by luiz on 25/04/17.
 */
@Path("/v2/checklists/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
public class ChecklistRelatorioResource {
    private final ChecklistRelatorioService service = new ChecklistRelatorioService();

    @GET
    @Path("/ambev-checklists-realizados-dia/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getChecklistsRealizadosDiaAmbevCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                              @QueryParam("dataInicial") final String dataInicial,
                                                              @QueryParam("dataFinal") final String dataFinal) {
        return outputStream ->
                service.getChecklistsRealizadosDiaAmbevCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/ambev-checklists-realizados-dia/report")
    public Report getChecklistsRealizadosDiaAmbevReport(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getChecklistsRealizadosDiaAmbevReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/ambev-extrato-checklists-realizados-dia/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getExtratoChecklistsRealizadosDiaAmbevCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                                     @QueryParam("dataInicial") final String dataInicial,
                                                                     @QueryParam("dataFinal") final String dataFinal) {
        return outputStream ->
                service.getExtratoChecklistsRealizadosDiaAmbevCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/ambev-extrato-checklists-realizados-dia/report")
    public Report getExtratoChecklistsRealizadosDiaAmbevReport(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getExtratoChecklistsRealizadosDiaAmbevReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/tempo-realizacao-checklists-motoristas/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getTempoRealizacaoChecklistsMotoristasCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                                     @QueryParam("dataInicial") final String dataInicial,
                                                                     @QueryParam("dataFinal") final String dataFinal) {
        return outputStream ->
                service.getTempoRealizacaoChecklistsMotoristasCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/tempo-realizacao-checklists-motoristas/report")
    public Report getTempoRealizacaoChecklistsMotoristasReport(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getTempoRealizacaoChecklistsMotoristasReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/listagem-modelos/report")
    public Report getListagemModelosChecklistReport(
            @QueryParam("codUnidades") final List<Long> codUnidades) throws ProLogException {
        return service.getListagemModelosChecklistReport(codUnidades);
    }

    @GET
    @Path("/listagem-modelos/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getListagemModelosChecklistCsv(@QueryParam("codUnidades") final List<Long> codUnidades) {
        return outputStream -> service.getListagemModelosChecklistCsv(outputStream, codUnidades);
    }

    @GET
    @Path("/ultimo-checklists-realizado-placa/report")
    public Report getUltimoChecklistRealizadoPlacaReport(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                         @QueryParam("codTiposVeiculos") final List<Long> codTiposVeiculos)
            throws ProLogException {
        return service.getUltimoChecklistRealizadoPlacaReport(codUnidades, codTiposVeiculos);
    }

    @GET
    @Path("/ultimo-checklists-realizado-placa/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getUltimoChecklistRealizadoPlacaCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                               @QueryParam("codTiposVeiculos") final
                                                               List<Long> codTiposVeiculos) {
        return outputStream ->
                service.getUltimoChecklistRealizadoPlacaCsv(
                        outputStream,
                        codUnidades,
                        codTiposVeiculos);
    }

    @GET
    @Path("/estratificacao-respostas-nok/{placa}/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getEstratificacaoRespostasNokCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                            @QueryParam("dataInicial") final String dataInicial,
                                                            @QueryParam("dataFinal") final String dataFinal) {
        return outputStream ->
                service.getEstratificacaoRespostasNokCsv(outputStream,
                                                         codUnidades,
                                                         dataInicial,
                                                         dataFinal);
    }

    @GET
    @Path("/estratificacao-respostas-nok/{placa}/report")
    public Report getEstratificacaoRespostasNokReport(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getEstratificacaoRespostasNokReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/resumo-checklists/{placa}/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getResumoChecklistsCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                  @QueryParam("dataInicial") final String dataInicial,
                                                  @QueryParam("dataFinal") final String dataFinal) {
        return outputStream -> service.getResumoChecklistsCsv(outputStream,
                                                              codUnidades,
                                                              dataInicial,
                                                              dataFinal);
    }

    @GET
    @Path("/resumo-checklists/{placa}/report")
    public Report getResumoChecklistsReport(@QueryParam("codUnidades") final List<Long> codUnidades,
                                            @QueryParam("dataInicial") final String dataInicial,
                                            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getResumoChecklistsReport(codUnidades, dataInicial, dataFinal);
    }
}