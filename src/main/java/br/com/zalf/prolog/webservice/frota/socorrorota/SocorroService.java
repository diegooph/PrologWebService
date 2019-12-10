package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.socorrorota._model.SocorroRotaAbertura;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SocorroService {
    private static final String TAG = SocorroService.class.getSimpleName();
    @NotNull
    private final SocorroDao dao = Injection.provideSocorroDao();

    @NotNull
    ResponseWithCod aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Solicitação de socorro criada com sucesso.",
                    dao.aberturaSocorro(socorroRotaAbertura));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao abrir uma solitação de socorro.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a abertura desta solicitação de socorro, " +
                            "tente novamente");
        }
    }
}