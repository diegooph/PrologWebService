package br.com.zalf.prolog.webservice.frota.checklist.dashboard;

import br.com.zalf.prolog.webservice.dashboard.components.charts.line.HorizontalLineChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dashboards/checklists")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DashboardChecklistResource {
    private final DashboardChecklistService service = new DashboardChecklistService();

    @GET
    @Path("/quantidade-checklists-ultimos-30-dias/{codComponente}")
    public HorizontalLineChartComponent getQtdChecklistsUltimos30DiasByTipo(
            @PathParam("codComponente") Integer codComponente,
            @QueryParam("codUnidades") List<Long> codUnidades) throws ProLogException {
        return service.getQtdChecklistsUltimos30DiasByTipo(codComponente, codUnidades);
    }
}