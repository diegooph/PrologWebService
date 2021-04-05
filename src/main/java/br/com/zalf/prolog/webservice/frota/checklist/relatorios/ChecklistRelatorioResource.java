package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoBackwardHelper;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.inject.Inject;
import javax.inject.Provider;
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

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

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
    @Path("/busca-resumo-checklists/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput buscaResumoChecklistsCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                    @QueryParam("codVeiculo") final Long codVeiculo,
                                                    @QueryParam("dataInicial") final String dataInicial,
                                                    @QueryParam("dataFinal") final String dataFinal) {
        return outputStream -> service.getResumoChecklistsCsv(outputStream,
                                                              codUnidades,
                                                              codVeiculo,
                                                              dataInicial,
                                                              dataFinal);
    }

    @GET
    @Path("/busca-resumo-checklists/report")
    public Report buscaResumoChecklistsReport(@QueryParam("codUnidades") final List<Long> codUnidades,
                                              @QueryParam("codVeiculo") final Long codVeiculo,
                                              @QueryParam("dataInicial") final String dataInicial,
                                              @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getResumoChecklistsReport(codUnidades, codVeiculo, dataInicial, dataFinal);
    }

    @GET
    @Path("/busca-estratificacao-respostas-nok/report")
    public Report buscaEstratificacaoRespostasNokReport(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("codVeiculo") final Long codVeiculo,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        return service.getEstratificacaoRespostasNokReport(codUnidades, codVeiculo, dataInicial, dataFinal);
    }

    @GET
    @Path("/busca-estratificacao-respostas-nok/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput buscaEstratificacaoRespostasNokCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                              @QueryParam("codVeiculo") final Long codVeiculo,
                                                              @QueryParam("dataInicial") final String dataInicial,
                                                              @QueryParam("dataFinal") final String dataFinal) {
        return outputStream ->
                service.getEstratificacaoRespostasNokCsv(outputStream, codUnidades, codVeiculo, dataInicial, dataFinal);
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

    /**
     * @deprecated Este método foi depreciado afim de criar um novo que recebe codVeiculo no lugar da placa.
     * <br>
     * {@link #buscaEstratificacaoRespostasNokCsv(List, Long, String, String)}
     * <br>
     * Porém há sistemas dependentes desse endpoint ainda (WS).
     */
    @Deprecated
    @GET
    @Path("/estratificacao-respostas-nok/{placa}/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getEstratificacaoRespostasNokCsv(@PathParam("placa") final String placa,
                                                            @QueryParam("codUnidades") final List<Long> codUnidades,
                                                            @QueryParam("dataInicial") final String dataInicial,
                                                            @QueryParam("dataFinal") final String dataFinal) {
        final Long codColaborador = this.colaboradorAutenticadoProvider.get().getCodigo();
        final Long codVeiculo = VeiculoBackwardHelper.getCodVeiculoByPlaca(codColaborador, placa);
        return outputStream ->
                service.getEstratificacaoRespostasNokCsv(outputStream,
                                                         codUnidades,
                                                         codVeiculo,
                                                         dataInicial,
                                                         dataFinal);
    }

    /**
     * @deprecated Este método foi depreciado afim de criar um novo que recebe codVeiculo no lugar da placa.
     * <br>
     * {@link #buscaEstratificacaoRespostasNokReport(List, Long, String, String)}
     * <br>
     * Porém há sistemas dependentes desse endpoint ainda (WS).
     */
    @Deprecated
    @GET
    @Path("/estratificacao-respostas-nok/{placa}/report")
    public Report getEstratificacaoRespostasNokReport(
            @PathParam("placa") final String placa,
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {

        final Long codColaborador = this.colaboradorAutenticadoProvider.get().getCodigo();
        final Long codVeiculo = VeiculoBackwardHelper.getCodVeiculoByPlaca(codColaborador, placa);
        return service.getEstratificacaoRespostasNokReport(codUnidades, codVeiculo, dataInicial, dataFinal);
    }

    /**
     * @deprecated Este método foi depreciado afim de criar um novo que recebe codVeiculo no lugar da placa.
     * <br>
     * {@link #buscaResumoChecklistsCsv(List, Long, String, String)}
     * <br>
     * Porém há sistemas dependentes desse endpoint ainda (WS).
     */
    @Deprecated
    @GET
    @Path("/resumo-checklists/{placa}/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getResumoChecklistsCsv(@PathParam("placa") final String placa,
                                                  @QueryParam("codUnidades") final List<Long> codUnidades,
                                                  @QueryParam("dataInicial") final String dataInicial,
                                                  @QueryParam("dataFinal") final String dataFinal) {
        final Long codColaborador = this.colaboradorAutenticadoProvider.get().getCodigo();
        final Long codVeiculo = VeiculoBackwardHelper.getCodVeiculoByPlaca(codColaborador, placa);
        return outputStream -> service.getResumoChecklistsCsv(outputStream,
                                                              codUnidades,
                                                              codVeiculo,
                                                              dataInicial,
                                                              dataFinal);
    }

    /**
     * @deprecated Este método foi depreciado afim de criar um novo que recebe codVeiculo no lugar da placa.
     * <br>
     * {@link #buscaResumoChecklistsReport(List, Long, String, String)}
     * <br>
     * Porém há sistemas dependentes desse endpoint ainda (WS).
     */
    @Deprecated
    @GET
    @Path("/resumo-checklists/{placa}/report")
    public Report getResumoChecklistsReport(@PathParam("placa") final String placa,
                                            @QueryParam("codUnidades") final List<Long> codUnidades,
                                            @QueryParam("dataInicial") final String dataInicial,
                                            @QueryParam("dataFinal") final String dataFinal) throws ProLogException {
        final Long codColaborador = this.colaboradorAutenticadoProvider.get().getCodigo();
        final Long codVeiculo = VeiculoBackwardHelper.getCodVeiculoByPlaca(codColaborador, placa);
        return service.getResumoChecklistsReport(codUnidades, codVeiculo, dataInicial, dataFinal);
    }
}