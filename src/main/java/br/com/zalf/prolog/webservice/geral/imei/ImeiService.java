package br.com.zalf.prolog.webservice.geral.imei;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.geral.imei.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class ImeiService {
    @NotNull
    private static final String TAG = ImeiService.class.getSimpleName();
    @NotNull
    private final ImeiDao dao = Injection.provideImeiDao();

    @NotNull
    public List<MarcaCelularSelecao> getMarcasCelular() throws ProLogException {
        try {
            return dao.getMarcasCelular();
        } catch (final Throwable throwable) {
            final String errorMessage = "Erro ao buscar as marcas de celular";
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar as marcas de celular, tente novamente");
        }
    }
}