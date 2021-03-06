package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.AvisoDelecaoTransferenciaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.VeiculoSelecaoTransferencia;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import br.com.zalf.prolog.webservice.integracao.router.RouterVeiculoTransferencia;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VeiculoTransferenciaService {
    @NotNull
    private static final String TAG = VeiculoTransferenciaService.class.getSimpleName();
    @NotNull
    private final VeiculoTransferenciaDao dao = Injection.provideVeiculoTransferenciaDao();

    @NotNull
    public final ResponseWithCod insertProcessoTransferenciaVeiculo(
            @NotNull final String userToken,
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Processo de transferĂȘncia realizado com sucesso",
                    RouterVeiculoTransferencia
                            .create(dao, userToken)
                            .insertProcessoTransferenciaVeiculo(
                                    processoTransferenciaVeiculo,
                                    Injection.provideDadosChecklistOfflineChangedListener()));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar processo de transferĂȘncia:", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar processo de transferĂȘncia, tente novamente");
        }
    }

    @NotNull
    public List<VeiculoSelecaoTransferencia> getVeiculosParaSelecaoTransferencia(final Long codUnidadeOrigem)
            throws ProLogException {
        try {
            return dao.getVeiculosParaSelecaoTransferencia(codUnidadeOrigem);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar veĂ­culos para transferĂȘncia:\n" +
                    "codUnidadeOrigem: %d\n", codUnidadeOrigem), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar veĂ­culos, tente novamente");
        }
    }

    @NotNull
    public List<ProcessoTransferenciaVeiculoListagem> getProcessosTransferenciaVeiculoListagem(
            final List<Long> codUnidadesOrigem,
            final List<Long> codUnidadesDestino,
            final String dataInicial,
            final String dataFinal) throws ProLogException {
        try {
            return dao.getProcessosTransferenciaVeiculoListagem(
                    codUnidadesOrigem,
                    codUnidadesDestino,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao listar processos de transferĂȘncia:\n" +
                    "codUnidadesOrigem: %s\n" +
                    "codUnidadesDestino: %s\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidadesOrigem, codUnidadesDestino, dataInicial, dataFinal), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao listar processos de transferĂȘncia, tente novamente");
        }
    }

    @NotNull
    public ProcessoTransferenciaVeiculoVisualizacao getProcessoTransferenciaVeiculoVisualizacao(
            final Long codProcessoTransferencia) throws ProLogException {
        try {
            return dao.getProcessoTransferenciaVeiculoVisualizacao(codProcessoTransferencia);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar processo de transferĂȘncia:\n" +
                    "codProcessoTransferencia: %d", codProcessoTransferencia), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar processo de transferĂȘncia, tente novamente");
        }
    }

    @NotNull
    public DetalhesVeiculoTransferido getDetalhesVeiculoTransferido(final Long codProcessoTransferencia,
                                                                    final Long codVeiculo) throws ProLogException {
        try {
            return dao.getDetalhesVeiculoTransferido(codProcessoTransferencia, codVeiculo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar detalhes de uma placa transferida:\n" +
                    "codProcessoTransferencia: %d\n" +
                    "codVeiculo: %d", codProcessoTransferencia, codVeiculo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar detalhes da placa transferida, tente novamente");
        }
    }

    @NotNull
    public AvisoDelecaoTransferenciaVeiculo buscaAvisoDelecaoAutomaticaPorTransferencia(@NotNull final Long codEmpresa) {
        try {
            return dao.buscaAvisoDelecaoAutomaticaPorTransferencia(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao verificar se empresa tem deleĂ§ĂŁo automĂĄtica de " +
                    "Ordens de ServiĂ§o habilitado:\ncodEmpresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao verificar se empresa tem deleĂ§ĂŁo automĂĄtica de " +
                            "Ordens de ServiĂ§o habilitado, tente novamente");
        }
    }

}
