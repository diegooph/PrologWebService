package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.dashboard;

import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.dashboard.components.charts.pie.PieChartComponent;
import br.com.zalf.prolog.webservice.dashboard.components.table.TableComponent;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 21/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dashboards/checklists/ordens-servico")
@Secured(permissions = Pilares.Frota.Relatorios.CHECKLIST)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DashboardChecklistOsResource {
    @NotNull
    private final DashboardChecklistOsService service = new DashboardChecklistOsService();

    @GET
    @Path("/quantidade-itens-os-abertos-por-prioridade/{codComponente}")
    public PieChartComponent getQtdItensOsAbertosByPrioridade(
            @PathParam("codComponente") final Integer codComponente,
            @QueryParam("codUnidades") final List<Long> codUnidades) throws ProLogException {
        return service.getQtdItensOsAbertosByPrioridade(codComponente, codUnidades);
    }

    @GET
    @Path("/placas-maior-quantidade-itens-os-abertos/{codComponente}")
    public TableComponent getPlacasMaiorQtdItensOsAbertos(
            @PathParam("codComponente") final Integer codComponente,
            @QueryParam("codUnidades") final List<Long> codUnidades) throws ProLogException {
        return service.getPlacasMaiorQtdItensOsAbertos(codComponente, codUnidades);
    }
    @GET
    @Path("/placas-bloqueadas/{codComponente}")
    public TableComponent getPlacasBloqueadas(
            @PathParam("codComponente") @Required final Integer codComponente,
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return service.getPlacasBloqueadas(codComponente, codUnidades);
    }
}