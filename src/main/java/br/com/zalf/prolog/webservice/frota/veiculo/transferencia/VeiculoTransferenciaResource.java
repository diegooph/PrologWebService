package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.AvisoDelecaoTransferenciaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoSelecaoTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@ConsoleDebugLog
@Path("veiculos/transferencias")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Secured(permissions = Pilares.Frota.TRANSFERENCIA_PNEUS_VEICULOS)
public final class VeiculoTransferenciaResource {
    @NotNull
    private final VeiculoTransferenciaService service = new VeiculoTransferenciaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    public ResponseWithCod insertProcessoTransferenciaVeiculo(
            @HeaderParam("Authorization") @Required final String userToken,
            @Required final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws ProLogException {
        return service.insertProcessoTransferenciaVeiculo(userToken, processoTransferenciaVeiculo);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/veiculos-selecao")
    public List<VeiculoSelecaoTransferencia> getVeiculosParaSelecaoTransferencia(
            @QueryParam("codUnidadeOrigem") @Required final Long codUnidadeOrigem) throws ProLogException {
        return service.getVeiculosParaSelecaoTransferencia(codUnidadeOrigem);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    public List<ProcessoTransferenciaVeiculoListagem> getProcessosTransferenciaVeiculoListagem(
            @QueryParam("codUnidadesOrigem") @Required final List<Long> codUnidadesOrigem,
            @QueryParam("codUnidadesDestino") @Required final List<Long> codUnidadesDestino,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) throws ProLogException {
        return service.getProcessosTransferenciaVeiculoListagem(
                codUnidadesOrigem,
                codUnidadesDestino,
                dataInicial,
                dataFinal);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/{codProcessoTransferencia}")
    public ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao(
            @PathParam("codProcessoTransferencia") @Required final Long codProcessoTransferencia) throws ProLogException {
        return service.getProcessoTransferenciaVeiculoVisualizacao(codProcessoTransferencia);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/{codProcessoTransferencia}/detalhes/{codVeiculo}")
    public DetalhesVeiculoTransferido getDetalhesVeiculoTransferido(
            @PathParam("codProcessoTransferencia") @Required final Long codProcessoTransferencia,
            @PathParam("codVeiculo") @Required final Long codVeiculo) throws ProLogException {
        return service.getDetalhesVeiculoTransferido(codProcessoTransferencia, codVeiculo);
    }

    @GET
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/aviso-transferencia")
    public AvisoDelecaoTransferenciaVeiculo buscaAvisoDelecaoAutomaticaPorTransferencia(
            @QueryParam("codEmpresa") @Required final Long codEmpresa) {
        return service.buscaAvisoDelecaoAutomaticaPorTransferencia(codEmpresa);
    }

}
