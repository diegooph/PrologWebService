package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.visualizacao.PneuTransferenciaVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Secured
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DummyPneuTransferenciaResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-visualizacao-list")
    public List<PneuTransferenciaVisualizacao> getPneuTransferenciaVisualizacao() {
        ensureDebugEnviroment();
        final List<PneuTransferenciaVisualizacao> transferencias = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            transferencias.add(PneuTransferenciaVisualizacao.createDummy());
        }
        return transferencias;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-listagem-list")
    public List<PneuTransferenciaListagem> getPneuTransferenciaListagem() {
        ensureDebugEnviroment();
        final List<PneuTransferenciaListagem> transferencias = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            transferencias.add(PneuTransferenciaListagem.createDummy());
        }
        return transferencias;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-realizacao")
    public PneuTransferenciaRealizacao getPneuTransferenciaRealizacao() {
        ensureDebugEnviroment();
        return PneuTransferenciaRealizacao.createDummy();
    }

    private void ensureDebugEnviroment() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Esse resource só pode ser utilizado em ambientes de testes");
        }
    }
}

