package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.Visao;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15/08/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargo")
    @Secured
    public Cargo getCargo() {
        return Cargo.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargos")
    @Secured
    public List<Cargo> getCargos() {
        final List<Cargo> cargos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cargos.add(Cargo.createDummy());
        }
        return cargos;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/visao")
    @Secured
    public Visao getVisao() {
        return Visao.createDummy();
    }
}