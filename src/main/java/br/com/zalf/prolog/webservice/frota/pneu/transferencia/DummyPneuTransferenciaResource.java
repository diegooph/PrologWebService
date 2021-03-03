package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Secured
@Path("/v2/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyPneuTransferenciaResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/pneu-transferencia-visualizacao-list")
    public List<PneuTransferenciaProcessoVisualizacao> getPneuTransferenciaVisualizacao() {
        ensureDebugEnviroment();
        final List<PneuTransferenciaProcessoVisualizacao> transferencias = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            transferencias.add(PneuTransferenciaProcessoVisualizacao.createDummy());
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

