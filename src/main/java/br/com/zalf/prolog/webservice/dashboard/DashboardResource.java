package br.com.zalf.prolog.webservice.dashboard;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dashboards")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DashboardResource {
    private final DashboardService service = new DashboardService();

    @GET
    @Path("/componentes")
    public List<DashboardPilarComponents> getComponentesColaborador(@HeaderParam("Authorization") String userToken) {
        return service.getComponentesColaborador(userToken);
    }
}