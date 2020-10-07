package br.com.zalf.prolog.webservice.frota.veiculo.historico.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 2020-09-29
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class HistoricoEdicaoVeiculoRelatorioService {
    @NotNull
    private static final String TAG = HistoricoEdicaoVeiculoRelatorioService.class.getSimpleName();
    @NotNull
    private final HistoricoEdicaoVeiculoRelatorioDao dao = Injection.provideHistoricoEdicaoVeiculoRelatorioDao();

    public void getHistoricoEdicaoVeiculoCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final Long codVeiculo) {
        try {
            dao.getHistoricoEdicaoVeiculoCsv(outputStream, codEmpresa, codVeiculo);
        } catch (final Throwable e) {
            Log.e(TAG,
                    String.format("Erro ao buscar histórico do veículo %d, da empresa %d.", codVeiculo, codEmpresa),
                    e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar histórico de edições, tente novamente.");
        }
    }
}
