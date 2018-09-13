package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import br.com.zalf.prolog.webservice.interceptors.debugenv.ResourceDebugOnly;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
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
@DebugLog
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargo")
    public Cargo getCargo() {
        return Cargo.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/cargo-list")
    public List<Cargo> getCargoList() {
        final List<Cargo> cargos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cargos.add(Cargo.createDummy());
        }
        return cargos;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/visao")
    public Visao getVisao() {
        return Visao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/tipo-intervalo")
    public TipoIntervalo getTipoIntervalo() {
        return TipoIntervalo.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/tipo-intervalo-list")
    public List<TipoIntervalo> getTipoIntervaloList() {
        final List<TipoIntervalo> tipos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tipos.add(TipoIntervalo.createDummy());
        }
        return tipos;
    }
}