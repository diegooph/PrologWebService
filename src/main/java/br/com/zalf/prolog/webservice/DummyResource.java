package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

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
        final Cargo cargo = new Cargo();
        cargo.setNome("Cargo Teste");
        cargo.setCodigo(1L);

        // Cria pilar frota.
        final List<Pilar> pilares = new ArrayList<>();
        final Pilar frota = new Pilar();
        frota.setCodigo(Pilares.FROTA);
        frota.setNome("Frota");

        // Cria função de realizar aferição.
        final List<FuncaoProLog> funcoesFrota = new ArrayList<>();
        final FuncaoProLog realizarAfericao = new FuncaoProLog();
        realizarAfericao.setCodigo(Pilares.Frota.Afericao.REALIZAR);
        funcoesFrota.add(realizarAfericao);

        frota.setFuncoes(funcoesFrota);
        pilares.add(frota);
        cargo.setPermissoes(pilares);
        return cargo;
    }
}
