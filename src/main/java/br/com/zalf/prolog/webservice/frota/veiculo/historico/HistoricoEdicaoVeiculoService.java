package br.com.zalf.prolog.webservice.frota.veiculo.historico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.HistoricoEdicaoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-09-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class HistoricoEdicaoVeiculoService {
    @NotNull
    private static final String TAG = HistoricoEdicaoVeiculoService.class.getSimpleName();
    @NotNull
    private final HistoricoEdicaoVeiculoDao dao = Injection.provideHistoricoEdicaoVeiculoDao();

    @NotNull
    public List<HistoricoEdicaoVeiculo> getHistoricoEdicaoVeiculo(@NotNull final Long codEmpresa,
                                                                  @NotNull final Long codVeiculo) {
        try {
            return dao.getHistoricoEdicaoVeiculo(codEmpresa, codVeiculo);
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
