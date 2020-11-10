package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.historico._model.VeiculoAcoplamentoHistoricoResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoHistoricoService {
    @NotNull
    private static final String TAG = VeiculoAcoplamentoHistoricoService.class.getSimpleName();
    @NotNull
    private final VeiculoAcoplamentoHistoricoDao dao = Injection.provideVeiculoAcoplamentoDao();

    @NotNull
    public Response getVeiculoAcoplamentosHistorico(@NotNull final List<Long> codUnidades,
                                                    @Nullable final List<Long> codVeiculos,
                                                    @Nullable final String dataInicial,
                                                    @Nullable final String dataFinal) throws ProLogException {
        try {
            final Optional<List<VeiculoAcoplamentoHistoricoResponse>> optional = dao.getVeiculoAcoplamentosHistorico(
                    codUnidades,
                    codVeiculos,
                    dataInicial != null ? ProLogDateParser.toLocalDate(dataInicial) : null,
                    dataFinal != null ? ProLogDateParser.toLocalDate(dataFinal) : null);
            if (optional.isPresent()) {
                return Response
                        .ok(optional.get())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return Response
                    .noContent()
                    .build();
        } catch (final Throwable e) {
            Log.e(TAG,
                    String.format("Erro ao buscar acoplamentos das unidades %s", codUnidades.toString()),
                    e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar acoplamentos, tente novamente.");
        }
    }
}