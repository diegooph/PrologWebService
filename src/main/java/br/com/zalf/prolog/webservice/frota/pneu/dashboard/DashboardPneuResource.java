package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.barchart.VerticalBarChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.densitychart.DensityChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.table.TableComponent;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioPneuService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zart on 09/01/2018.
 */
@Path("/dashboards/pneus")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DashboardPneuResource {
    private final RelatorioPneuService relatorioPneuService = new RelatorioPneuService();
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
    @Path("/quantidade-afericoes-semana-atual/{codComponente}")
    public VerticalComboChartComponent getQtAfericoesByTipoByData(@PathParam("codComponente") Integer codComponente,
                                                                  @QueryParam("codUnidades") List<Long> codUnidades) {
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
    public DensityChartComponent getMenorSulcoEPressaoPneu(@PathParam("codComponente") Integer codComponente,
                                                           @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getMenorSulcoEPressaoPneu(codComponente, codUnidades);
    }

    @GET
    @Path("media-tempo-conserto-servicos-por-tipo/{codComponente}")
    public VerticalBarChartComponent getMdTempoConsertoServicoPorTipo(@PathParam("codComponente") Integer codComponente,
                                                                      @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getMediaTempoConsertoServicoPorTipo(codComponente, codUnidades);
    }
}
