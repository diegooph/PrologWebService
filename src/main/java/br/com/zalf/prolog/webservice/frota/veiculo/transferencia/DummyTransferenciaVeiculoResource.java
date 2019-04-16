package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.DummyData;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debugenv.ResourceDebugOnly;

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
@ResourceDebugOnly
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class DummyTransferenciaVeiculoResource extends DummyData {

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-realizacao")
    public ProcessoTransferenciaVeiculoRealizacao getProcessoTransferenciaVeiculoRealizacao() {
        return ProcessoTransferenciaVeiculoRealizacao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-listagem")
    public List<ProcessoTransferenciaVeiculoListagem> getProcessoTransferenciaVeiculoListagem() {
        final List<ProcessoTransferenciaVeiculoListagem> transferencias = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            transferencias.add(ProcessoTransferenciaVeiculoListagem.createDummy());
        }
        return transferencias;
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-visualizacao")
    public ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao() {
        return ProcessoTransferenciaVeiculoVisualizacao.createDummy();
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/processo-transferencia-veiculo-detalhes-veiculo-transferido")
    public DetalhesVeiculoTransferido getDetalhesVeiculoTransferido() {
        return DetalhesVeiculoTransferido.createDummy();
    }
}