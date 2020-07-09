package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
            return internalGetCodVeiculosByPlacas(codColaborador,
                    new ArrayList<String>(){{ add(placa); }}, true).get(0);
    }

    @NotNull
    public static Long getCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                               @NotNull final List<String> placas) {
        return internalGetCodVeiculosByPlacas(codColaborador, placas, false).get(0);
    }

    @NotNull
    public static List<Long> internalGetCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                                            @NotNull final List<String> placas,
                                                            boolean single) {

        try {
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            return veiculoDao.getCodVeiculosByPlacas(codColaborador, placas);
        } catch (final Throwable t) {
            String errorMsg = "Erro ao buscar os códigos do veículo.";
            if(single){
                errorMsg = "Erro ao buscar o código do veículo.";
            }
            Log.e(TAG, errorMsg, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, errorMsg);
        }
    }
}
