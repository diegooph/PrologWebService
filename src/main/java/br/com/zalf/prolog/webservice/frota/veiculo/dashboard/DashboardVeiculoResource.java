package br.com.zalf.prolog.webservice.frota.veiculo.dashboard;

import br.com.zalf.prolog.webservice.dashboard.components.QuantidadeItemComponent;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dashboards/pneus")
@Secured(permissions = Pilares.Frota.Relatorios.PNEU)
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashboardVeiculoResource {
    private final DashboardVeiculoService service = new DashboardVeiculoService();

    @GET
    @Path("/quantidade-veiculos-ativos-com-pneus-aplicados/{codComponente}")
    public QuantidadeItemComponent getQtdPneusByStatus(@QueryParam("codUnidades") List<Long> codUnidades,
                                                       @PathParam("codComponente") Integer codComponente) {
        return service.getQtdVeiculosAtivosComPneuAplicado(codUnidades, codComponente);
    }
}