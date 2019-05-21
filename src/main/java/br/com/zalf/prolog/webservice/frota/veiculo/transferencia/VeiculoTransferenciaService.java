package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
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
    private final VeiculoTransferenciaDao dao = Injection.provideVeiculoTransferenciaDaoImpl();

    @NotNull
    public final ResponseWithCod insertProcessoTransferenciaVeiculo(
            final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Processo de transferência realizado com sucesso",
                    dao.insertProcessoTranseferenciaVeiculo(processoTransferenciaVeiculo));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar processo de transferência:", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar processo de transferência, tente novamente");
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
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao listar processos de transferência:\n" +
                    "codUnidadesOrigem: %s\n" +
                    "codUnidadesDestino: %s\n" +
                    "dataInicial: %s\n" +
                    "dataFinal: %s", codUnidadesOrigem, codUnidadesDestino, dataInicial, dataFinal), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao listar processos de transferência, tente novamente");
        }
    }
}
