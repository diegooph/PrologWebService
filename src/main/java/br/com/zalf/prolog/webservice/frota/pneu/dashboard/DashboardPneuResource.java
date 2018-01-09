package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioService;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.QtAfericao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.Date;
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

    private RelatorioService service = new RelatorioService();

    @GET
    @Path("/pneus-por-status")
    public Map<String, Long> getQtPneusByStatus(@QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtPneusByStatus(codUnidades);
    }

    @GET
    @Path("/quantidade-afericoes-semana-atual")
    public List<QtAfericao> getQtAfericoesByTipoByData(@QueryParam("codUnidade") List<Long> codUnidades) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return service.getQtAfericoesByTipoByData(
                new Date(System.currentTimeMillis()),
                DateUtils.toSqlDate(calendar.getTime()), codUnidades);
    }

    @GET
    @Path("quantidade-servicos-abertos-por-tipo")
    public Map<String, Integer> getServicosEmAbertoByTipo(@QueryParam("codUnidade") List<Long> codUnidades) {
        return service.getServicosEmAbertoByTipo(codUnidades);
    }

    @GET
    @Path("quantidade-placas-afericoes-vencidas")
    public Map<String, Integer> getQtdPlacasAfericaoVencida(@QueryParam("codUnidade") List<Long> codUnidades) {
        return service.getQtdPlacasAfericaoVencida(codUnidades);
    }
}
