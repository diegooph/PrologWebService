package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import org.jetbrains.annotations.NotNull;

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
            Log.e(TAG, "Erro ao realizar processo de transferência", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar processo de transferência, tente novamente");
        }
    }
}
