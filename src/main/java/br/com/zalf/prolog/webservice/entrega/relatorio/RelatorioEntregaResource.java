package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.entrega.indicador.Indicador;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
@Path("/v2/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Entrega.Relatorios.INDICADORES)
public class RelatorioEntregaResource {

    private final RelatorioEntregaService service = new RelatorioEntregaService();

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<IndicadorAcumulado> getAcumuladoIndicadores(@QueryParam("dataInicial") final Long dataInicial,
                                                            @QueryParam("dataFinal") final Long dataFinal,
                                                            @PathParam("codEmpresa") final String codEmpresa,
                                                            @PathParam("codRegional") final String codRegional,
                                                            @PathParam("codUnidade") final String codUnidade,
                                                            @PathParam("equipe") final String equipe) {
        return service.getAcumuladoIndicadores(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/extratos/{indicador}/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") final Long dataInicial,
                                               @QueryParam("dataFinal") final Long dataFinal,
                                               @PathParam("codRegional") final String codRegional,
                                               @PathParam("codEmpresa") final String codEmpresa,
                                               @PathParam("codUnidade") final String codUnidade,
                                               @PathParam("equipe") final String equipe,
                                               @PathParam("cpf") final String cpf,
                                               @PathParam("indicador") final String indicador) {
        return service.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa,
                codUnidade, equipe, cpf, indicador);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/diarios/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<ConsolidadoDia> getConsolidadoDia(@QueryParam("dataInicial") final Long dataInicial,
                                                  @QueryParam("dataFinal") final Long dataFinal,
                                                  @PathParam("codRegional") final String codRegional,
                                                  @PathParam("codEmpresa") final String codEmpresa,
                                                  @PathParam("codUnidade") final String codUnidade,
                                                  @PathParam("equipe") final String equipe,
                                                  @QueryParam("limit") final int limit,
                                                  @QueryParam("offset") final int offset) {
        return service.getConsolidadoDia(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, limit, offset);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/mapas/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<MapaEstratificado> getMapasEstratificados(@QueryParam("data") final Long data,
                                                          @PathParam("codEmpresa") final String codEmpresa,
                                                          @PathParam("codRegional") final String codRegional,
                                                          @PathParam("codUnidade") final String codUnidade,
                                                          @PathParam("equipe") final String equipe) {
        return service.getMapasEstratificados(data, codEmpresa, codRegional, codUnidade, equipe);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/graficos/{indicador}/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<DadosGrafico> getDadosGrafico(@QueryParam("dataInicial") final Long dataInicial,
                                              @QueryParam("dataFinal") final Long dataFinal,
                                              @PathParam("codRegional") final String codRegional,
                                              @PathParam("codEmpresa") final String codEmpresa,
                                              @PathParam("codUnidade") final String codUnidade,
                                              @PathParam("equipe") final String equipe,
                                              @PathParam("indicador") final String indicador) {
        return service.getDadosGrafico(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, indicador);
    }

    @GET
    @Secured(permissions = {
            Pilares.Entrega.Relatorios.PRODUTIVIDADE,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.Upload.VERIFICACAO_DADOS})
    @Path("/mapas/estratificados/{codUnidade}/csv")
    public StreamingOutput getEstratificacaoMapasCsv(@PathParam("codUnidade") final Long codUnidade,
                                                     @QueryParam("dataInicial") final Long dataInicial,
                                                     @QueryParam("dataFinal") final Long dataFinal) {
        return outputStream -> service.getEstratificacaoMapasCsv(codUnidade, dataInicial, dataFinal, outputStream);
    }

    @GET
    @Secured(permissions = {
            Pilares.Entrega.Relatorios.PRODUTIVIDADE,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.Upload.VERIFICACAO_DADOS})
    @Path("/mapas/estratificados/{codUnidade}/report")
    public Report getEstratificacaoMapasReport(@PathParam("codUnidade") final Long codUnidade,
                                               @QueryParam("dataInicial") final Long dataInicial,
                                               @QueryParam("dataFinal") final Long dataFinal) {
        return service.getEstratificacaoMapasReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/extratos/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/csv")
    public StreamingOutput getExtratoMapasIndicadorCsv(@PathParam("codEmpresa") final Long codEmpresa,
                                                       @PathParam("codRegional") final String codRegional,
                                                       @PathParam("codUnidade") final String codUnidade,
                                                       @PathParam("cpf") final String cpf,
                                                       @QueryParam("dataInicial") final Long dataInicial,
                                                       @QueryParam("dataFinal") final Long dataFinal,
                                                       @PathParam("equipe") final String equipe) {
        return outputstream -> service.getExtratoMapasIndicadorCsv(codEmpresa, codRegional, codUnidade, cpf,
                dataInicial, dataFinal, equipe, outputstream);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/extratos/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/report")
    public Report getExtratoMapasIndicadorReport(@PathParam("codEmpresa") final Long codEmpresa,
                                                 @PathParam("codRegional") final String codRegional,
                                                 @PathParam("codUnidade") final String codUnidade,
                                                 @PathParam("cpf") final String cpf,
                                                 @QueryParam("dataInicial") final Long dataInicial,
                                                 @QueryParam("dataFinal") final Long dataFinal,
                                                 @PathParam("equipe") final String equipe) {
        return service.getExtratoMapasIndicadorReport(codEmpresa, codRegional, codUnidade, cpf, dataInicial, dataFinal, equipe);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/consolidados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/csv")
    public StreamingOutput getConsolidadoMapasIndicadorCsv(@PathParam("codEmpresa") final Long codEmpresa,
                                                           @PathParam("codRegional") final String codRegional,
                                                           @PathParam("codUnidade") final String codUnidade,
                                                           @PathParam("equipe") final String codEquipe,
                                                           @PathParam("cpf") final String cpf,
                                                           @QueryParam("dataInicial") final Long dataInicial,
                                                           @QueryParam("dataFinal") final Long dataFinal) {
        return outputstream -> service.getConsolidadoMapasIndicadorCsv(
                outputstream,
                codEmpresa,
                codRegional,
                codUnidade,
                codEquipe,
                cpf,
                dataInicial,
                dataFinal);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/consolidados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/report")
    public Report getConsolidadoMapasIndicadorReport(@PathParam("codEmpresa") final Long codEmpresa,
                                                     @PathParam("codRegional") final String codRegional,
                                                     @PathParam("codUnidade") final String codUnidade,
                                                     @PathParam("equipe") final String codEquipe,
                                                     @PathParam("cpf") final String cpf,
                                                     @QueryParam("dataInicial") final Long dataInicial,
                                                     @QueryParam("dataFinal") final Long dataFinal) {
        return service.getConsolidadoMapasIndicadorReport(
                codEmpresa,
                codRegional,
                codUnidade,
                codEquipe,
                cpf,
                dataInicial,
                dataFinal);
    }
}