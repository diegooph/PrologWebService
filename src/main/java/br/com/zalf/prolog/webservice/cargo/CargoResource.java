package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.cargo.model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo.model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo.model.CargoTodos;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("cargos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class CargoResource {
    @NotNull
    private final CargoService service = new CargoService();

    @GET
    @Secured
    @Path("/todos")
    public List<CargoTodos> getTodosCargosUnidade(@QueryParam("codUnidade") @Required Long codUnidade)
            throws ProLogException {
        return service.getTodosCargosUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/em-uso")
    public List<CargoEmUso> getCargosEmUsoUnidade(
            @QueryParam("codUnidade") @Required Long codUnidade) throws ProLogException {
        return service.getCargosEmUsoUnidade(codUnidade);
    }

    @GET
    @Secured
    @Path("/nao-utilizados")
    public List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade(
            @QueryParam("codUnidade") @Required Long codUnidade) throws ProLogException {
        return service.getCargosNaoUtilizadosUnidade(codUnidade);
    }
}