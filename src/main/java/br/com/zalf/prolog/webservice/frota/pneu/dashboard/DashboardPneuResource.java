package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.combochart.VerticalComboChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.piechart.PieChartComponent;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioPneuService;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by Zart on 09/01/2018.
 */
@Path("/dashboards/pneus")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashboardPneuResource {
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
    @Path("quantidade-servicos-abertos-por-tipo")
    public Map<String, Integer> getServicosEmAbertoByTipo(@QueryParam("codUnidade") List<Long> codUnidades) {
        return relatorioPneuService.getServicosEmAbertoByTipo(codUnidades);
    }

    @GET
    @Path("quantidade-placas-afericoes-vencidas")
    public Map<String, Integer> getQtdPlacasAfericaoVencida(@QueryParam("codUnidade") List<Long> codUnidades) {
        return relatorioPneuService.getQtdPlacasAfericaoVencida(codUnidades);
    }

    @GET
    @Path("media-tempo-conserto-servicos-por-tipo")
    public Map<String, Integer> getMdTempoConsertoServicoPorTipo(@QueryParam("codUnidade") List<Long> codUnidades) {
        return relatorioPneuService.getMdTempoConsertoServicoPorTipo(codUnidades);
    }

    @GET
    @Path("quantidade-km-rodado-com-servico-aberto")
    public Map<String, Integer> getQtKmRodadoServicoAberto(@QueryParam("codUnidade") List<Long> codUnidades) {
        return relatorioPneuService.getQtKmRodadoServicoAberto(codUnidades);
    }

    @GET
    @Path("placas-com-pneus-abaixo-limite-milimetragem")
    public Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(@QueryParam("codUnidade") List<Long> codUnidades) {
        return relatorioPneuService.getPlacasComPneuAbaixoLimiteMilimetragem(codUnidades);
    }

    @GET
    @Path("listagem-menor-sulco-pneus")
    public Map<String, Double> getMenorSulcoPneu(@QueryParam("codUnidade") List<Long> codUnidades) {
        return relatorioPneuService.getMenorSulcoPneu(codUnidades);
    }
}
