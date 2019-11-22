package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios.AfericaoRelatorioService;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.relatorios.MovimentacaoRelatorioService;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.Faixa;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

@Path("/pneus/relatorios")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 55,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class RelatorioPneuResource {
    @GET
    @Path("/farol-afericao/csv")
    @Produces("application/csv")
    public StreamingOutput getFarolAfericao(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> new RelatorioPneuService().getFarolAfericaoCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/desgaste-irregular/csv")
    @Produces("application/csv")
    public StreamingOutput getPneusComDesgasteIrregularCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("statusPneu") @Required final String statusPneu) {
        return outputStream -> new RelatorioPneuService().getPneusComDesgasteIrregularCsv(
                outputStream,
                codUnidades,
                statusPneu);
    }

    @GET
    @Path("/desgaste-irregular/report")
    public Report getPneusComDesgasteIrregularReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("statusPneu") @Required final String statusPneu) throws ProLogException {
        return new RelatorioPneuService().getPneusComDesgasteIrregularReport(codUnidades, statusPneu);
    }

    @GET
    @Path("/status-atual-pneus/csv")
    @Produces("application/csv")
    public StreamingOutput getStatusAtualPneusCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) {
        return outputStream -> new RelatorioPneuService().getStatusAtualPneusCsv(outputStream, codUnidades);
    }

    @GET
    @Path("/status-atual-pneus/report")
    public Report getStatusAtualPneusReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades)
            throws ProLogException {
        return new RelatorioPneuService().getStatusAtualPneusReport(codUnidades);
    }

    @GET
    @Path("/km-rodado-por-pneu-por-vida/csv")
    @Produces("application/csv")
    public StreamingOutput getKmRodadoPorPneuPorVidaCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) {
        return outputStream -> new RelatorioPneuService().getKmRodadoPorPneuPorVidaCsv(outputStream, codUnidades);
    }

    @GET
    @Path("/km-rodado-por-pneu-por-vida/report")
    public Report getKmRodadoPorPneuPorVidaReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades)
            throws ProLogException {
        return new RelatorioPneuService().getKmRodadoPorPneuPorVidaReport(codUnidades);
    }

    @GET
    @Path("/afericoes-avulsas/csv")
    @Produces("application/csv")
    public StreamingOutput getAfericoesAvulsasCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> new RelatorioPneuService().getAfericoesAvulsasCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/afericoes-avulsas/report")
    public Report getAfericoesAvulsasReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                            @QueryParam("dataInicial") @Required final String dataInicial,
                                            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        return new RelatorioPneuService().getAfericoesAvulsasReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/pneus-faixa-sulco")
    public List<Faixa> getQtdPneusByFaixaSulco(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                               @QueryParam("status") @Required final List<String> status) {
        return new RelatorioPneuService().getQtdPneusByFaixaSulco(codUnidades, status);
    }

    @GET
    @Path("/previsao-trocas-estratificados/csv")
    @Produces("application/csv")
    public StreamingOutput getPrevisaoTrocaEstratificadoCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws RuntimeException {
        return outputStream -> new RelatorioPneuService().getPrevisaoTrocaEstratificadoCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/previsao-trocas-estratificados/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getPrevisaoTrocaEstratificadoReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                      @QueryParam("dataInicial") @Required final String dataInicial,
                                                      @QueryParam("dataFinal") @Required final String dataFinal) {
        return new RelatorioPneuService().getPrevisaoTrocaEstratificadoReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/previsao-trocas-consolidados/csv")
    @Produces("application/csv")
    public StreamingOutput getPrevisaoTrocaConsolidadoCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws RuntimeException {
        return outputStream -> new RelatorioPneuService().getPrevisaoTrocaConsolidadoCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/previsao-trocas-consolidados/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getPrevisaoTrocaConsolidadoReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                    @QueryParam("dataInicial") @Required final String dataInicial,
                                                    @QueryParam("dataFinal") @Required final String dataFinal) {
        return new RelatorioPneuService().getPrevisaoTrocaConsolidadoReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/aderencias-placas-afericao/csv")
    @Produces("application/csv")
    public StreamingOutput getAderenciaPlacasCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws RuntimeException {
        return outputStream -> new RelatorioPneuService().getAderenciaPlacasCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/aderencias-placas-afericao/report")
    public Report getAderenciaPlacasReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                           @QueryParam("dataInicial") @Required final String dataInicial,
                                           @QueryParam("dataFinal") @Required final String dataFinal) {
        return new RelatorioPneuService().getAderenciaPlacasReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/pneus-descartados/report")
    public Report getPneusDescartadosReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                            @QueryParam("dataInicial") @Required final String dataInicial,
                                            @QueryParam("dataFinal") @Required final String dataFinal) {
        return new RelatorioPneuService().getPneusDescartadosReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/pneus-descartados/csv")
    public StreamingOutput getPneusDescartadosCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws RuntimeException {
        return outputStream -> new RelatorioPneuService().getPneusDescartadosCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/dados-ultima-afericao/csv")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public StreamingOutput getDadosUltimaAfericaoCsv(@QueryParam("codUnidades") @Required final List<Long> codUnidades)
            throws RuntimeException {
        return outputStream -> new RelatorioPneuService().getDadosUltimaAfericaoCsv(outputStream, codUnidades);
    }

    @GET
    @Path("/dados-ultima-afericao/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getDadosUltimaAfericaoReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades) {
        return new RelatorioPneuService().getDadosUltimaAfericaoReport(codUnidades);
    }

    @GET
    @Path("/resumo-geral-pneus/csv")
    public StreamingOutput getResumoGeralPneusCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("status-pneu") @Optional final String status) throws RuntimeException {
        return outputStream -> new RelatorioPneuService().getResumoGeralPneusCsv(outputStream, codUnidades, status);
    }

    @GET
    @Path("/resumo-geral-pneus/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report getResumoGeralPneusReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                            @QueryParam("status-pneu") @Optional final String status) {
        return new RelatorioPneuService().getResumoGeralPneusReport(codUnidades, status);
    }

    @GET
    @Path("/dados-gerais-afericoes/csv")
    @Produces("application/csv")
    public StreamingOutput getDadosGeraisAfericoesCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> new AfericaoRelatorioService().getDadosGeraisAfericoesCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/dados-gerais-afericoes/report")
    public Report getDadosGeraisAfericoesReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                @QueryParam("dataInicial") @Required final String dataInicial,
                                                @QueryParam("dataFinal") @Required final String dataFinal)
            throws ProLogException {
        return new AfericaoRelatorioService().getDadosGeraisAfericoesReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/cronograma-afericoes-placas/csv")
    @Produces("application/csv")
    public StreamingOutput getCronogramaAfericoesPlacasCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @HeaderParam("Authorization") @Required final String userToken) {
        return outputStream -> new AfericaoRelatorioService()
                .getCronogramaAfericoesPlacasCsv(outputStream, codUnidades, userToken);
    }

    @GET
    @Path("/cronograma-afericoes-placas/report")
    public Report getCronogramaAfericoesPlacasReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return new AfericaoRelatorioService().getCronogramaAfericoesPlacasReport(codUnidades, userToken);
    }

    @GET
    @Path("/dados-gerais-movimentacoes/csv")
    @Produces("application/csv")
    public StreamingOutput getDadosGeraisMovimentacoesCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> new MovimentacaoRelatorioService().getDadosGeraisMovimentacoesCsv(
                outputStream,
                codUnidades,
                dataInicial,
                dataFinal);
    }

    @GET
    @Path("/dados-gerais-movimentacoes/report")
    public Report getDadosGeraisMovimentacoesReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                    @QueryParam("dataInicial") @Required final String dataInicial,
                                                    @QueryParam("dataFinal") @Required final String dataFinal)
            throws ProLogException {
        return new MovimentacaoRelatorioService().getDadosGeraisMovimentacoesReport(codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/vencimento-dot/csv")
    @Produces("application/csv")
    public StreamingOutput getVencimentoDotCsv(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @HeaderParam("Authorization") @Required final String userToken) {
        return outputStream -> new RelatorioPneuService()
                .getVencimentoDotCsv(outputStream, codUnidades, userToken);
    }

    @GET
    @Path("/vencimento-dot/report")
    public Report getVencimentoDotReport(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return new RelatorioPneuService().getVencimentoDotReport(codUnidades, userToken);
    }

    /**
     * @deprecated at 2018-06-18. Use {@link RelatorioPneuResource#getQtdPneusByFaixaSulco(List, List)} instead.
     */
    @GET
    @Path("/resumoSulcos")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public List<Faixa> DEPRECATED_GET_QTD_PNEUS_BY_FAIXA_SULCO(
            @QueryParam("codUnidades") List<String> codUnidades,
            @QueryParam("status") List<String> status) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18. Use {@link RelatorioPneuResource#getPrevisaoTrocaEstratificadoCsv(List, String, String)} instead.
     */
    @GET
    @Path("/previsao-trocas/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput DEPRECATED_GET_PREVISAO_TROCA_CSV(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18. Use {@link RelatorioPneuResource#getPrevisaoTrocaEstratificadoReport(List, String, String)} instead.
     */
    @GET
    @Path("/previsao-trocas/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report DEPRECATED_GET_PREVISAO_TROCA_REPORT(@PathParam("codUnidade") Long codUnidade,
                                                       @QueryParam("dataInicial") long dataInicial,
                                                       @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getPrevisaoTrocaConsolidadoCsv(List, String, String)} instead.
     */
    @GET
    @Path("/previsao-trocas/consolidados/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput DEPRECATED_GET_PREVISAO_TROCA_CONSOLIDADO_CSV(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getPrevisaoTrocaConsolidadoReport(List, String, String)} instead.
     */
    @GET
    @Path("/previsao-trocas/consolidados/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report DEPRECATED_GET_PREVISAO_TROCA_CONSOLIDADO_REPORT(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    @GET
    @Path("/resumoPressao")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    @Deprecated
    public List<Faixa> DEPRECATED_GET_QTD_PNEUS_BY_FAIXA_PRESSAO(@QueryParam("codUnidades") List<String> codUnidades,
                                                                 @QueryParam("status") List<String> status) {
        return new RelatorioPneuService().getQtPneusByFaixaPressao(codUnidades, status);
    }

    @GET
    @Path("/aderencia/{codUnidade}/{ano}/{mes}")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    @Deprecated
    public List<Aderencia> DEPRECATED_GET_ADERENCIA_BY_UNIDADE(@PathParam("ano") int ano,
                                                               @PathParam("mes") int mes,
                                                               @PathParam("codUnidade") Long codUnidade) {
        return new RelatorioPneuService().getAderenciaByUnidade(ano, mes, codUnidade);
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getAderenciaPlacasCsv(List, String, String)} instead.
     */
    @GET
    @Path("/aderencias/placas/{codUnidade}/csv")
    @Produces("application/csv")
    public StreamingOutput DEPRECATED_GET_ADERENCIA_PLACAS_CSV(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getAderenciaPlacasReport(List, String, String)} instead.
     */
    @GET
    @Path("/aderencias/placas/{codUnidade}/report")
    public Report DEPRECATED_GET_ADERENCIA_PLACAS_REPORT(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getPneusDescartadosReport(List, String, String)} instead.
     */
    @GET
    @Path("/pneus-descartados/{codUnidade}/report")
    public Report DEPRECATED_GET_PNEUS_DESCARTADOS_REPORT(
            @PathParam("codUnidade") @Required Long codUnidade,
            @QueryParam("dataInicial") @Required Long dataInicial,
            @QueryParam("dataFinal") @Required Long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getPneusDescartadosCsv(List, String, String)} instead.
     */
    @GET
    @Path("/pneus-descartados/{codUnidade}/csv")
    public StreamingOutput DEPRECATED_GET_PNEUS_DESCARTADOS_CSV(
            @PathParam("codUnidade") @Required Long codUnidade,
            @QueryParam("dataInicial") @Required Long dataInicial,
            @QueryParam("dataFinal") @Required Long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getDadosUltimaAfericaoCsv(List)} instead.
     */
    @GET
    @Path("/dados-ultima-afericao/{codUnidade}/csv")
    public StreamingOutput DEPRECATED_GET_DADOS_ULTIMA_AFERICAO_CSV(@PathParam("codUnidade") Long codUnidade)
            throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getDadosUltimaAfericaoReport(List)} instead.
     */
    @GET
    @Path("/dados-ultima-afericao/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report DEPRECATED_GET_DADOS_ULTIMA_AFERICAO_REPORT(@PathParam("codUnidade") Long codUnidade)
            throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getResumoGeralPneusCsv(List, String)} instead.
     */
    @GET
    @Path("/resumo-geral-pneus/{codUnidade}/csv")
    public StreamingOutput DEPRECATED_GET_RESUMO_GERAL_PNEUS_CSV(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("status-pneu") @Optional final String status) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated at 2018-06-18.
     * Use {@link RelatorioPneuResource#getResumoGeralPneusReport(List, String)} instead.
     */
    @GET
    @Path("/resumo-geral-pneus/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    public Report DEPRECATED_GET_RESUMO_GERAL_PNEUS_REPORT(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("status-pneu") @Optional final String status) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated in v2_57. Use {@link RelatorioPneuResource#getDadosUltimaAfericaoCsv(List)} instead.
     */
    @GET
    @Path("/afericoes/resumo/pneus/{codUnidade}/csv")
    @Deprecated
    public StreamingOutput DEPRECATED_DADOS_ULTIMA_AFERICAO_CSV(@PathParam("codUnidade") Long codUnidade)
            throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated in v2_57. Use {@link RelatorioPneuResource#getDadosUltimaAfericaoReport(List)} instead.
     */
    @GET
    @Path("/afericoes/resumo/pneus/{codUnidade}/report")
    @Secured(permissions = Pilares.Frota.Relatorios.PNEU)
    @Deprecated
    public Report DEPRECATED_DADOS_ULTIMA_AFERICAO_REPORT(@PathParam("codUnidade") Long codUnidade)
            throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated in v0.0.11. Use {@link RelatorioPneuResource#getAderenciaPlacasCsv(List, String, String)} instead.
     */
    @GET
    @Path("/aderencia/placas/{codUnidade}/csv")
    @Produces("application/csv")
    @Deprecated
    public StreamingOutput DEPRECATED_ADERENCIA_CSV(@PathParam("codUnidade") Long codUnidade,
                                                    @QueryParam("dataInicial") long dataInicial,
                                                    @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }

    /**
     * @deprecated in v0.0.11. Use {@link RelatorioPneuResource#getAderenciaPlacasReport(List, String, String)} instead.
     */
    @GET
    @Path("/aderencia/placas/{codUnidade}/report")
    @Deprecated
    public Report DEPRECATED_ADERENCIA_REPORT(@PathParam("codUnidade") Long codUnidade,
                                              @QueryParam("dataInicial") long dataInicial,
                                              @QueryParam("dataFinal") long dataFinal) throws ProLogException {
        throw new GenericException("Este relatório está disponível em uma nova versão do ProLog." +
                "\nPor favor, atualize sua aplicação");
    }
}