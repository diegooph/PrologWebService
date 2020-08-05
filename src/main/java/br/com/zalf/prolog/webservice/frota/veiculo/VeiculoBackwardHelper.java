package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2020-07-09
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public class VeiculoBackwardHelper {
    @NotNull
    private static final String TAG = VeiculoBackwardHelper.class.getSimpleName();

    @NotNull
    public static Long getCodVeiculoByPlaca(@NotNull final Long codColaborador,
                                            @NotNull final String placa) {
        return internalGetCodVeiculosByPlacas(codColaborador, Collections.singletonList(placa)).get(0);
    }

    @NotNull
    public static List<Long> getCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                                    @NotNull final List<String> placas) {
        return internalGetCodVeiculosByPlacas(codColaborador, placas);
    }

    @NotNull
    public static List<Long> internalGetCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                                            @NotNull final List<String> placas) {

        try {
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            return veiculoDao.getCodVeiculosByPlacas(codColaborador, placas);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os códigos dos veículos para as placas: " + placas, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os códigos dos veículos.");
        }
    }
}
