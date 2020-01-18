package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
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
@Path("/relatorios")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Entrega.Relatorios.INDICADORES)
public class RelatorioEntregaResource {

    private RelatorioEntregaService service = new RelatorioEntregaService();

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<IndicadorAcumulado> getAcumuladoIndicadores(@QueryParam("dataInicial") Long dataInicial,
                                                            @QueryParam("dataFinal") Long dataFinal,
                                                            @PathParam("codEmpresa") String codEmpresa,
                                                            @PathParam("codRegional") String codRegional,
                                                            @PathParam("codUnidade") String codUnidade,
                                                            @PathParam("equipe") String equipe){
        return service.getAcumuladoIndicadores(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/extratos/{indicador}/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}")
    public List<Indicador> getExtratoIndicador(@QueryParam("dataInicial") Long dataInicial,
                                               @QueryParam("dataFinal") Long dataFinal,
                                               @PathParam("codRegional") String codRegional,
                                               @PathParam("codEmpresa") String codEmpresa,
                                               @PathParam("codUnidade") String codUnidade,
                                               @PathParam("equipe") String equipe,
                                               @PathParam("cpf") String cpf,
                                               @PathParam("indicador") String indicador){
        return service.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa,
                codUnidade, equipe, cpf, indicador);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/diarios/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<ConsolidadoDia> getConsolidadoDia(@QueryParam("dataInicial") Long dataInicial,
                                                  @QueryParam("dataFinal") Long dataFinal,
                                                  @PathParam("codRegional") String codRegional,
                                                  @PathParam("codEmpresa") String codEmpresa,
                                                  @PathParam("codUnidade") String codUnidade,
                                                  @PathParam("equipe") String equipe,
                                                  @QueryParam("limit") int limit,
                                                  @QueryParam("offset") int offset){
        return service.getConsolidadoDia(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, limit, offset);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/mapas/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<MapaEstratificado> getMapasEstratificados(@QueryParam("data") Long data,
                                                          @PathParam("codEmpresa") String codEmpresa,
                                                          @PathParam("codRegional") String codRegional,
                                                          @PathParam("codUnidade") String codUnidade,
                                                          @PathParam("equipe") String equipe){
        return service.getMapasEstratificados(data, codEmpresa, codRegional, codUnidade, equipe);
    }

    @GET
    @Secured
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/acumulados/graficos/{indicador}/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}")
    public List<DadosGrafico> getDadosGrafico(@QueryParam("dataInicial") Long dataInicial,
                                              @QueryParam("dataFinal") Long dataFinal,
                                              @PathParam("codRegional") String codRegional,
                                              @PathParam("codEmpresa") String codEmpresa,
                                              @PathParam("codUnidade") String codUnidade,
                                              @PathParam("equipe") String equipe,
                                              @PathParam("indicador") String indicador){
        return service.getDadosGrafico(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe, indicador);
    }

    @GET
    @Secured(permissions = {
            Pilares.Entrega.Relatorios.PRODUTIVIDADE,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.Upload.VERIFICACAO_DADOS})
    @Path("/mapas/estratificados/{codUnidade}/csv")
    public StreamingOutput getEstratificacaoMapasCsv(@PathParam("codUnidade") Long codUnidade,
                                                     @QueryParam("dataInicial") Long dataInicial,
                                                     @QueryParam("dataFinal") Long dataFinal) {
        return outputStream -> service.getEstratificacaoMapasCsv(codUnidade, dataInicial, dataFinal, outputStream);
    }

    @GET
    @Secured(permissions = {
            Pilares.Entrega.Relatorios.PRODUTIVIDADE,
            Pilares.Entrega.Relatorios.INDICADORES,
            Pilares.Entrega.Upload.VERIFICACAO_DADOS})
    @Path("/mapas/estratificados/{codUnidade}/report")
    public Report getEstratificacaoMapasReport(@PathParam("codUnidade") Long codUnidade,
                                               @QueryParam("dataInicial") Long dataInicial,
                                               @QueryParam("dataFinal") Long dataFinal) {
        return service.getEstratificacaoMapasReport(codUnidade, dataInicial, dataFinal);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/extratos/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/csv")
    public StreamingOutput getExtratoMapasIndicadorCsv(@PathParam("codEmpresa") Long codEmpresa,
                                                       @PathParam("codRegional") String codRegional,
                                                       @PathParam("codUnidade") String codUnidade,
                                                       @PathParam("cpf") String cpf,
                                                       @QueryParam("dataInicial") Long dataInicial,
                                                       @QueryParam("dataFinal") Long dataFinal,
                                                       @PathParam("equipe") String equipe) {
        return outputstream -> service.getExtratoMapasIndicadorCsv(codEmpresa, codRegional, codUnidade, cpf,
                dataInicial, dataFinal, equipe, outputstream);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/extratos/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/report")
    public Report getExtratoMapasIndicadorReport(@PathParam("codEmpresa") Long codEmpresa,
                                                 @PathParam("codRegional") String codRegional,
                                                 @PathParam("codUnidade") String codUnidade,
                                                 @PathParam("cpf") String cpf,
                                                 @QueryParam("dataInicial") Long dataInicial,
                                                 @QueryParam("dataFinal") Long dataFinal,
                                                 @PathParam("equipe") String equipe) {
        return service.getExtratoMapasIndicadorReport(codEmpresa, codRegional, codUnidade, cpf, dataInicial, dataFinal, equipe);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/consolidados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/csv")
    public StreamingOutput getConsolidadoMapasIndicadorCsv(@PathParam("codEmpresa") Long codEmpresa,
                                                       @PathParam("codRegional") String codRegional,
                                                       @PathParam("codUnidade") String codUnidade,
                                                       @PathParam("cpf") String cpf,
                                                       @QueryParam("dataInicial") Long dataInicial,
                                                       @QueryParam("dataFinal") Long dataFinal,
                                                       @PathParam("equipe") String equipe) {
        return outputstream -> service.getConsolidadoMapasIndicadorCsv(codEmpresa, codRegional, codUnidade, cpf,
                dataInicial, dataFinal, equipe, outputstream);
    }

    @GET
    @Secured(permissions = {Pilares.Entrega.Relatorios.PRODUTIVIDADE, Pilares.Entrega.Relatorios.INDICADORES})
    @Path("/indicadores/consolidados/{codEmpresa}/{codRegional}/{codUnidade}/{equipe}/{cpf}/report")
    public Report getConsolidadoMapasIndicadorReport(@PathParam("codEmpresa") Long codEmpresa,
                                                 @PathParam("codRegional") String codRegional,
                                                 @PathParam("codUnidade") String codUnidade,
                                                 @PathParam("cpf") String cpf,
                                                 @QueryParam("dataInicial") Long dataInicial,
                                                 @QueryParam("dataFinal") Long dataFinal,
                                                 @PathParam("equipe") String equipe) {
        return service.getConsolidadoMapasIndicadorReport(codEmpresa, codRegional, codUnidade, cpf, dataInicial, dataFinal, equipe);
    }
}