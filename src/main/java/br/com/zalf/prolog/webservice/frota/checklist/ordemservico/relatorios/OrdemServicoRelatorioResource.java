package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by luiz on 26/04/17.
 */
@Path("/checklists/ordens-servico/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
public class OrdemServicoRelatorioResource {
    private final OrdemServicoRelatorioService service = new OrdemServicoRelatorioService();

    @GET
    @Path("/itens-maior-quantidade-nok/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getItensMaiorQuantidadeNokCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                         @QueryParam("dataInicial") String dataInicial,
                                                         @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getItensMaiorQuantidadeNokCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/itens-maior-quantidade-nok/report")
    public Report getItensMaiorQuantidadeNokReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                                   @QueryParam("dataInicial") String dataInicial,
                                                   @QueryParam("dataFinal") String dataFinal) {
        return service.getItensMaiorQuantidadeNokReport(
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/media-tempo-conserto-itens-os/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getMediaTempoConsertoItemCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                        @QueryParam("dataInicial") String dataInicial,
                                                        @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getMediaTempoConsertoItemCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/media-tempo-conserto-itens-os/report")
    public Report getMediaTempoConsertoItemReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                                  @QueryParam("dataInicial") String dataInicial,
                                                  @QueryParam("dataFinal") String dataFinal) {
        return service.getMediaTempoConsertoItemReport(
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/produtividade-mecanicos/csv")
    @Produces("application/csv")
    @UsedBy(platforms = Platform.WEBSITE)
    public StreamingOutput getProdutividadeMecanicosCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                        @QueryParam("dataInicial") String dataInicial,
                                                        @QueryParam("dataFinal") String dataFinal) {
        return outputStream -> service.getProdutividadeMecanicosCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/produtividade-mecanicos/report")
    public Report getProdutividadeMecanicosReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                                  @QueryParam("dataInicial") String dataInicial,
                                                  @QueryParam("dataFinal") String dataFinal) {
        return service.getProdutividadeMecanicosReport(
                codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/estratificacao-os/report")
    public Report getEstratificacaoOsReport(@QueryParam("codUnidades") List<Long> codUnidades,
                                            @QueryParam("placa") String placa,
                                            @QueryParam("statusOs") String statusOs,
                                            @QueryParam("statusItem") String statusItem,
                                            @QueryParam("dataInicialAbertura") String dataInicialAbertura,
                                            @QueryParam("dataFinalAbertura") String dataFinalAbertura,
                                            @QueryParam("dataInicialResolucao") String dataInicialResolucao,
                                            @QueryParam("dataFinalResolucao") String dataFinalResolucao) {
        return service.getEstratificacaoOsReport(
                codUnidades,
                placa,
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
    public StreamingOutput getEstratificacaoOsCsv(@QueryParam("codUnidades") List<Long> codUnidades,
                                                  @QueryParam("placa") String placa,
                                                  @QueryParam("statusOs") String statusOs,
                                                  @QueryParam("statusItem") String statusItem,
                                                  @QueryParam("dataInicialAbertura") String dataInicialAbertura,
                                                  @QueryParam("dataFinalAbertura") String dataFinalAbertura,
                                                  @QueryParam("dataInicialResolucao") String dataInicialResolucao,
                                                  @QueryParam("dataFinalResolucao") String dataFinalResolucao) {
        return outputStream -> service.getEstratificacaoOsCsv(
                outputStream,
                codUnidades,
                placa,
                statusOs,
                statusItem,
                dataInicialAbertura,
                dataFinalAbertura,
                dataInicialResolucao,
                dataFinalResolucao);
    }
}