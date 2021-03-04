package br.com.zalf.prolog.webservice.seguranca.relato.dashboard;

import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2/8/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/dashboards/relatos")
@Secured(permissions = Pilares.Seguranca.Relato.RELATORIOS)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DashboardRelatoResource {
    @NotNull
    private final DashboardRelatoService service = new DashboardRelatoService();

    @GET
    @Path("/quantidade-relatos-realizados-hoje/{codComponente}")
    public final QuantidadeItemComponent getQtdRelatosRealizadosHoje(@PathParam("codComponente") Integer codComponente,
                                                                     @QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtdRelatosRealizadosHoje(codComponente, codUnidades);
    }

    @GET
    @Path("/quantidade-relatos-pendentes-por-status/{codComponente}")
    public PieChartComponent getQtdRelatosPendentesByStatus(
            @PathParam("codComponente") final Integer codComponente,
            @QueryParam("codUnidades") final List<Long> codUnidades) throws ProLogException {
        return service.getQtdRelatosPendentesByStatus(codComponente, codUnidades);
    }
}