package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by luiz on 26/04/17.
 */
@Path("/v2/checklists/ordens-servico/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
public class OrdemServicoRelatorioResource {
    @NotNull
    private final OrdemServicoRelatorioService service = new OrdemServicoRelatorioService();

    @GET
    @Path("/itens-maior-quantidade-nok/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getItensMaiorQuantidadeNokCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                         @QueryParam("dataInicial") final String dataInicial,
                                                         @QueryParam("dataFinal") final String dataFinal) {
        return outputStream -> service.getItensMaiorQuantidadeNokCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/itens-maior-quantidade-nok/report")
    public Report getItensMaiorQuantidadeNokReport(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                   @QueryParam("dataInicial") final String dataInicial,
                                                   @QueryParam("dataFinal") final String dataFinal) {
        return service.getItensMaiorQuantidadeNokReport(
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/media-tempo-conserto-itens-os/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getMediaTempoConsertoItemCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                        @QueryParam("dataInicial") final String dataInicial,
                                                        @QueryParam("dataFinal") final String dataFinal) {
        return outputStream -> service.getMediaTempoConsertoItemCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/media-tempo-conserto-itens-os/report")
    public Report getMediaTempoConsertoItemReport(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                  @QueryParam("dataInicial") final String dataInicial,
                                                  @QueryParam("dataFinal") final String dataFinal) {
        return service.getMediaTempoConsertoItemReport(
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/produtividade-mecanicos/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getProdutividadeMecanicosCsv(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                        @QueryParam("dataInicial") final String dataInicial,
                                                        @QueryParam("dataFinal") final String dataFinal) {
        return outputStream -> service.getProdutividadeMecanicosCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/produtividade-mecanicos/report")
    public Report getProdutividadeMecanicosReport(@QueryParam("codUnidades") final List<Long> codUnidades,
                                                  @QueryParam("dataInicial") final String dataInicial,
                                                  @QueryParam("dataFinal") final String dataFinal) {
        return service.getProdutividadeMecanicosReport(
                codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-os/report")
    public Report getEstratificacaoOsReportDeprecated(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("statusOs") final String statusOs,
            @QueryParam("statusItem") final String statusItem,
            @QueryParam("dataInicialAbertura") final String dataInicialAbertura,
            @QueryParam("dataFinalAbertura") final String dataFinalAbertura,
            @QueryParam("dataInicialResolucao") final String dataInicialResolucao,
            @QueryParam("dataFinalResolucao") final String dataFinalResolucao) {
        return service.getEstratificacaoOsReport(
                codUnidades,
                statusOs,
                statusItem,
                dataInicialAbertura,
                dataFinalAbertura,
                dataInicialResolucao,
                dataFinalResolucao);
    }

    @GET
    @Path("/estratificacao-os/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getEstratificacaoOsCsvDeprecated(
            @QueryParam("codUnidades") final List<Long> codUnidades,
            @QueryParam("statusOs") final String statusOs,
            @QueryParam("statusItem") final String statusItem,
            @QueryParam("dataInicialAbertura") final String dataInicialAbertura,
            @QueryParam("dataFinalAbertura") final String dataFinalAbertura,
            @QueryParam("dataInicialResolucao") final String dataInicialResolucao,
            @QueryParam("dataFinalResolucao") final String dataFinalResolucao) {
        return outputStream -> service.getEstratificacaoOsCsv(
                outputStream,
                codUnidades,
                statusOs,
                statusItem,
                dataInicialAbertura,
                dataFinalAbertura,
                dataInicialResolucao,
                dataFinalResolucao);
    }
}