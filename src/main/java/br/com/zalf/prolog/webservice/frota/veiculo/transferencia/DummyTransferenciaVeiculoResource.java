package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Secured
@Path("/dummies")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyTransferenciaVeiculoResource {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-list")
    public ProcessoTransferenciaVeiculoRealizacao getProcessoTransferenciaVeiculoRealizacao() {
        ensureDebugEnviroment();
        return ProcessoTransferenciaVeiculoRealizacao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-list")
    public List<ProcessoTransferenciaVeiculoListagem> getProcessoTransferenciaVeiculoListagem() {
        ensureDebugEnviroment();
        final List<ProcessoTransferenciaVeiculoListagem> transferencias = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            transferencias.add(ProcessoTransferenciaVeiculoListagem.createDummy());
        }
        return transferencias;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-visualizacao")
    public List<ProcessoTransferenciaVeiculoVisualizacao> getProcessoTransferenciaVeiculoVisualizacao() {
        ensureDebugEnviroment();
        final List<ProcessoTransferenciaVeiculoVisualizacao> processo = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            processo.add(ProcessoTransferenciaVeiculoVisualizacao.createDummy());
        }
        return processo;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/detalhes-veiculo-transferido")
    public DetalhesVeiculoTransferido getDetalhesVeiculoTransferido() {
        ensureDebugEnviroment();
        return DetalhesVeiculoTransferido.createDummy();
    }

    private void ensureDebugEnviroment() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Esse resource só pode ser utilizado em ambientes de testes");
        }
    }
}
