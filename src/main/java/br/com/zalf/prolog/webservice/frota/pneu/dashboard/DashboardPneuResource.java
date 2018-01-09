package br.com.zalf.prolog.webservice.frota.pneu.dashboard;

import br.com.zalf.prolog.webservice.frota.pneu.relatorios.RelatorioService;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.QtAfericao;
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

    private RelatorioService service = new RelatorioService();

    @GET
    @Path("/agrupados/status")
    public Map<String,Long> getQtPneusByStatus(@QueryParam("codUnidades") List<Long> codUnidades) {
        return service.getQtPneusByStatus(codUnidades);
    }

    @GET
    @Path("/afericoes/quantidades")
    public List<QtAfericao> getQtAfericoesByTipoByData(@QueryParam("dataInicial") Long dataInicial,
                                                       @QueryParam("dataFinal") Long dataFinal,
                                                       @QueryParam("codUnidade") List<Long> codUnidades) {
        return service.getQtAfericoesByTipoByData(dataInicial, dataFinal, codUnidades);
    }
}
