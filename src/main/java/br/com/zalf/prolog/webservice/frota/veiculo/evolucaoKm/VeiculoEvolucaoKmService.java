package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model.VeiculoEvolucaoKmResponse;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoEvolucaoKmService {
    @NotNull
    private static final String TAG = VeiculoEvolucaoKmService.class.getSimpleName();
    @NotNull
    private final VeiculoEvolucaoKmDao dao = Injection.provideVeiculoEvolucaoKmDao();

    @NotNull
    public Response getVeiculoEvolucaoKm(@NotNull final Long codEmpresa,
                                         @NotNull final Long codVeiculo,
                                         @NotNull final String dataInicial,
                                         @NotNull final String dataFinal) throws ProLogException {
        try {
            final Optional<VeiculoEvolucaoKmResponse> optional = dao.getVeiculoEvolucaoKm(codEmpresa,
                                                                                          codVeiculo,
                                                                                          PrologDateParser.toLocalDate(
                                                                                                  dataInicial),
                                                                                          PrologDateParser.toLocalDate(
                                                                                                  dataFinal));
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
                    String.format("Erro ao buscar evolução de km do veículo de código %d, da empresa %d.", codVeiculo,
                            codEmpresa),
                    e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar a evolução de km, tente novamente.");
        }
    }
}