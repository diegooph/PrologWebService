package br.com.zalf.prolog.webservice.cargo;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.cargo._model.CargoEmUso;
import br.com.zalf.prolog.webservice.cargo._model.CargoNaoUtilizado;
import br.com.zalf.prolog.webservice.cargo._model.CargoSelecao;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.debugenv.ResourceDebugOnly;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dummies")
@DebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyCargoResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargos-todos-unidade")
    public List<CargoSelecao> getCargosTodosUnidade() {
        final List<CargoSelecao> cargos = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            cargos.add(CargoSelecao.createDummy());
        }
        return cargos;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargos-em-uso-unidade")
    public List<CargoEmUso> getCargosEmUsoUnidade() {
        final List<CargoEmUso> cargos = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            cargos.add(CargoEmUso.createDummy());
        }
        return cargos;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargos-nao-utilizados-unidade")
    public List<CargoNaoUtilizado> getCargosNaoUtilizadosUnidade() {
        final List<CargoNaoUtilizado> cargos = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            cargos.add(CargoNaoUtilizado.createDummy());
        }
        return cargos;
    }
}