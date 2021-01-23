package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.AvisoDelecaoTransferenciaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoSelecaoTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface VeiculoTransferenciaDao {

    @NotNull
    Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable;

    @NotNull
    Long insertProcessoTransferenciaVeiculo(
            @NotNull final Connection conn,
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable;

    @NotNull
    List<VeiculoSelecaoTransferencia> getVeiculosParaSelecaoTransferencia(
            @NotNull final Long codUnidadeOrigem) throws Throwable;

    @NotNull
    List<ProcessoTransferenciaVeiculoListagem> getProcessosTransferenciaVeiculoListagem(
            @NotNull final List<Long> codUnidadesOrigem,
            @NotNull final List<Long> codUnidadesDestino,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao(
            @NotNull final Long codProcessoTransferencia) throws Throwable;

    @NotNull
    DetalhesVeiculoTransferido getDetalhesVeiculoTransferido(@NotNull final Long codProcessoTransferencia,
                                                             @NotNull final Long codVeiculo) throws Throwable;

    @NotNull
    AvisoDelecaoTransferenciaVeiculo buscaAvisoDelecaoAutomaticaPorTransferencia(@NotNull final Long codEmpresa)
            throws Throwable;
}
