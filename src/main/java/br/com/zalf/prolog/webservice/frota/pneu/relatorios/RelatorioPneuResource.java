package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.sql.SQLException;
import java.util.List;

@Path("/pneus/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 51,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class RelatorioPneuResource {
    private final RelatorioPneuService service = new RelatorioPneuService();

    /**
     * @deprecated at 2018-06-18. Use {@link RelatorioPneuResource#getQtdPneusByFaixaSulco(List, List)} instead.
     */
    @GET
    @Path("/resumoSulcos")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public List<Faixa> DEPRECATEDgetQtPneusByFaixaSulco(@QueryParam("codUnidades") List<String> codUnidades,
                                                        @QueryParam("status") List<String> status) {
        //TODO - Lançar Exception
        return null;
    }

    @GET
    @Path("/pneus-faixa-sulco")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public List<Faixa> getQtdPneusByFaixaSulco(@QueryParam("codUnidades") final List<Long> codUnidades,
                                               @QueryParam("status") final List<String> status) {
        return service.getQtdPneusByFaixaSulco(codUnidades, status);
    }

    @GET
    @Path("/previsao-trocas/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getPrevisaoTrocaCsv(@PathParam("codUnidade") Long codUnidade,
                                               @QueryParam("dataInicial") long dataInicial,
                                               @QueryParam("dataFinal") long dataFinal) throws RuntimeException {
        return outputStream -> service.getPrevisaoTrocaCsv(codUnidade, dataInicial, dataFinal, outputStream);
    }

    @GET
    @Path("/previsao-trocas/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getPrevisaoTrocaReport(@PathParam("codUnidade") Long codUnidade,
                                         @QueryParam("dataInicial") long dataInicial,
                                         @QueryParam("dataFinal") long dataFinal) {
        return service.getPrevisaoTrocaReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/previsao-trocas/consolidados/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getPrevisaoTrocaConsolidadoCsv(@PathParam("codUnidade") Long codUnidade,
                                                          @QueryParam("dataInicial") long dataInicial,
                                                          @QueryParam("dataFinal") long dataFinal) throws
            RuntimeException {
        return outputStream -> service.getPrevisaoTrocaConsolidadoCsv(codUnidade, dataInicial, dataFinal, outputStream);
    }

    @GET
    @Path("/previsao-trocas/consolidados/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getPrevisaoTrocaConsolidadoReport(@PathParam("codUnidade") Long codUnidade,
                                                    @QueryParam("dataInicial") long dataInicial,
                                                    @QueryParam("dataFinal") long dataFinal) throws SQLException {
        return service.getPrevisaoTrocaConsolidadoReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/aderencias/placas/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput getAderenciaPlacasCsv(@PathParam("codUnidade") Long codUnidade,
                                                 @QueryParam("dataInicial") long dataInicial,
                                                 @QueryParam("dataFinal") long dataFinal) throws RuntimeException {
        return outputStream -> service.getAderenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
    }

    @GET
    @Path("/aderencias/placas/{codUnidade}/report")
    public Report getAderenciaPlacasReport(@PathParam("codUnidade") Long codUnidade,
                                           @QueryParam("dataInicial") long dataInicial,
                                           @QueryParam("dataFinal") long dataFinal) throws SQLException {
        return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
    }


    @GET
    @Path("/pneus-descartados/{codUnidade}/report")
    public Report getPneusDescartadosReport(@PathParam("codUnidade") @Required Long codUnidade,
                                            @QueryParam("dataInicial") @Required Long dataInicial,
                                            @QueryParam("dataFinal") @Required Long dataFinal) {
        return service.getPneusDescartadosReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/pneus-descartados/{codUnidade}/csv")
    public StreamingOutput getPneusDescartadosCsv(@PathParam("codUnidade") @Required Long codUnidade,
                                                  @QueryParam("dataInicial") @Required Long dataInicial,
                                                  @QueryParam("dataFinal") @Required Long dataFinal) throws
            RuntimeException {
        return outputStream -> service.getPneusDescartadosCsv(outputStream, codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Path("/dados-ultima-afericao/{codUnidade}/csv")
    public StreamingOutput getDadosUltimaAfericaoCsv(@PathParam("codUnidade") Long codUnidade) throws RuntimeException {
        return outputStream -> service.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
    }

    @GET
    @Path("/dados-ultima-afericao/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getDadosUltimaAfericaoReport(@PathParam("codUnidade") Long codUnidade) {
        return service.getDadosUltimaAfericaoReport(codUnidade);
    }

    @GET
    @Path("/resumo-geral-pneus/{codUnidade}/csv")
    public StreamingOutput getResumoGeralPneusCsv(@PathParam("codUnidade") @Required final Long codUnidade,
                                                  @QueryParam("status-pneu") @Optional final String status) throws RuntimeException {
        return outputStream -> service.getResumoGeralPneusCsv(codUnidade, status, outputStream);
    }

    @GET
    @Path("/resumo-geral-pneus/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getResumoGeralPneusReport(@PathParam("codUnidade") @Required final Long codUnidade,
                                            @QueryParam("status-pneu") @Optional final String status) {
        return service.getResumoGeralPneusReport(codUnidade, status);
    }

    @GET
    @Path("/resumoPressao")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    @Deprecated
    public List<Faixa> DEPRECATED_GET_QTD_PNEUS_BY_FAIXA_PRESSAO(@QueryParam("codUnidades") List<String> codUnidades,
                                                                 @QueryParam("status") List<String> status) {
        return service.getQtPneusByFaixaPressao(codUnidades, status);
    }

    @GET
    @Path("/aderencia/{codUnidade}/{ano}/{mes}")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    @Deprecated
    public List<Aderencia> DEPRECATED_GET_ADERENCIA_BY_UNIDADE(@PathParam("ano") int ano,
                                                               @PathParam("mes") int mes,
                                                               @PathParam("codUnidade") Long codUnidade) {
        return service.getAderenciaByUnidade(ano, mes, codUnidade);
    }

    /**
     * @deprecated in v2_57. Use {@link RelatorioPneuResource#getDadosUltimaAfericaoCsv(Long)} instead.
     */
    @GET
    @Path("/afericoes/resumo/pneus/{codUnidade}/csv")
    @Deprecated
    public StreamingOutput DEPRECATED_DADOS_ULTIMA_AFERICAO_CSV(@PathParam("codUnidade") Long codUnidade) throws
            RuntimeException {
        return outputStream -> service.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
    }

    /**
     * @deprecated in v2_57. Use {@link RelatorioPneuResource#getDadosUltimaAfericaoReport(Long)} instead.
     */
    @GET
    @Path("/afericoes/resumo/pneus/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    @Deprecated
    public Report DEPRECATED_DADOS_ULTIMA_AFERICAO_REPORT(@PathParam("codUnidade") Long codUnidade) {
        return service.getDadosUltimaAfericaoReport(codUnidade);
    }

    /**
     * @deprecated in v0.0.11. Use {@link RelatorioPneuResource#getAderenciaPlacasReport(Long, long, long)} instead.
     */
    @GET
    @Path("/aderencia/placas/{codUnidade}/report")
    @Deprecated
    public Report DEPRECATED_ADERENCIA_REPORT(@PathParam("codUnidade") Long codUnidade,
                                              @QueryParam("dataInicial") long dataInicial,
                                              @QueryParam("dataFinal") long dataFinal) throws SQLException {
        return service.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
    }

    /**
     * @deprecated in v0.0.11. Use {@link RelatorioPneuResource#getAderenciaPlacasCsv(Long, long, long)} instead.
     */
    @GET
    @Path("/aderencia/placas/{codUnidade}/csv")
    @Produces("application/csv")
    @Deprecated
    public StreamingOutput DEPRECATED_ADERENCIA_CSV(@PathParam("codUnidade") Long codUnidade,
                                                    @QueryParam("dataInicial") long dataInicial,
                                                    @QueryParam("dataFinal") long dataFinal) throws RuntimeException {
        return outputStream -> service.getAderenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
    }
}