package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.bar.VerticalBarChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.combo.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.line.HorizontalLineChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.scatter.ScatterChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.table.TableComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zart on 09/01/2018.
 */
@Path("/v2/dashboards/pneus")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DashboardPneuResource {
    private final DashboardPneuService service = new DashboardPneuService();

    @GET
    @Path("/pneus-por-status/{codComponente}")
    public PieChartComponent getQtdPneusByStatus(@PathParam("codComponente") Integer codComponente,
                                                 @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtdPneusByStatus(codComponente, codUnidades);
    }

    @GET
    @Path("quantidade-pneus-pressao-incorreta/{codComponente}")
    public QuantidadeItemComponent getQtdPneusPressaoIncorreta(@PathParam("codComponente") Integer codComponente,
                                                               @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtdPneusPressaoIncorreta(codComponente, codUnidades);
    }

    @GET
    @Path("quantidade-pneus-cadastrados/{codComponente}")
    public QuantidadeItemComponent getQtdPneusCadastrados(@PathParam("codComponente") Integer codComponente,
                                                          @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtdPneusCadastrados(codComponente, codUnidades);
    }

    @GET
    @Path("/quantidade-afericoes-semana-atual/{codComponente}")
    public VerticalComboChartComponent getQtAfericoesByTipoByData(@PathParam("codComponente") Integer codComponente,
                                                                  @QueryParam("codUnidades") List<Long> codUnidades)
            throws ProLogException {
        return service.getQtdAfericoesUltimaSemana(codComponente, codUnidades);
    }

    @GET
    @Path("quantidade-servicos-abertos-por-tipo/{codComponente}")
    public VerticalBarChartComponent getServicosEmAbertoByTipo(@PathParam("codComponente") Integer codComponente,
                                                               @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getServicosEmAbertoByTipo(codComponente, codUnidades);
    }

    @GET
    @Path("quantidade-placas-afericoes-vencidas-e-no-prazo/{codComponente}")
    public PieChartComponent getStatusPlacasAfericao(@PathParam("codComponente") Integer codComponente,
                                                     @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getStatusPlacasAfericao(codComponente, codUnidades);
    }

    @GET
    @Path("placas-com-pneus-abaixo-limite-milimetragem/{codComponente}")
    public TableComponent getPlacasComPneuAbaixoLimiteMilimetragem(@PathParam("codComponente") Integer codComponente,
                                                                   @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getPlacasComPneuAbaixoLimiteMilimetragem(codComponente, codUnidades);
    }

    @GET
    @Path("quantidade-km-rodado-com-servico-aberto/{codComponente}")
    public TableComponent getQtdKmRodadoServicoAberto(@PathParam("codComponente") Integer codComponente,
                                                      @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtdKmRodadoComServicoEmAberto(codComponente, codUnidades);
    }

    @GET
    @Path("menor-sulco-e-pressao-pneus/{codComponente}")
    public ScatterChartComponent getMenorSulcoEPressaoPneu(@PathParam("codComponente") Integer codComponente,
                                                           @QueryParam("codUnidades") List<Long> codUnidades)
            throws ProLogException {
        return service.getMenorSulcoEPressaoPneu(codComponente, codUnidades);
    }

    @GET
    @Path("media-tempo-conserto-servicos-por-tipo/{codComponente}")
    public VerticalBarChartComponent getMdTempoConsertoServicoPorTipo(@PathParam("codComponente") Integer codComponente,
                                                                      @QueryParam("codUnidades") List<Long>
                                                                              codUnidades) {
        return service.getMediaTempoConsertoServicoPorTipo(codComponente, codUnidades);
    }

    @GET
    @Path("/quantidade-pneus-descartados-por-motivo/{codComponente}")
    public TableComponent getQuantidadePneusDescartadosPorMotivo(@PathParam("codComponente") Integer codComponente,
                                                                 @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQuantidadePneusDescartadosPorMotivo(codComponente, codUnidades);
    }

    @GET
    @Path("/quantidade-dias-afericoes-vencidas/{codComponente}")
    public TableComponent getQtdDiasAfericoesVencidas(
            @PathParam("codComponente") @Required final Integer codComponente,
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return service.getQtdDiasAfericoesVencidas(codComponente, codUnidades);
    }

    @GET
    @Path("/quantidade-afericoes-por-tipo-ultimos-30-dias/{codComponente}")
    public HorizontalLineChartComponent getQtdAfericoesRealizadasPorDiaByTipo(
            @PathParam("codComponente") @Required final Integer codComponente,
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return service.getQtdAfericoesRealizadasPorDiaByTipoInterval30Days(codComponente, codUnidades);
    }
}
