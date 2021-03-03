package br.com.zalf.prolog.webservice.frota.socorrorota.dashboard;

import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-31
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Path("/v2/dashboards/socorro-rota")
@Secured
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashboardSocorroRotaResource {

    @NotNull
    private final DashboardSocorroRotaService service = new DashboardSocorroRotaService();

    @GET
    @Path("/quantidade-socorros-por-status/{codComponente}")
    @Secured(permissions = {Pilares.Frota.SocorroRota.VISUALIZAR_SOCORROS_E_RELATORIOS})
    public @NotNull PieChartComponent getQtdSocorroRotaPorStatus(@PathParam("codComponente") final Integer codComponente,
                                                                 @QueryParam("codUnidades") final List<Long> codUnidades) throws ProLogException {
        return service.getQtdSocorroRotaPorStatus(codComponente, codUnidades);
    }

}
