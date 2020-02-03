package br.com.zalf.prolog.webservice.messaging;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.messaging._model.PushColaboradorCadastro;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PushService {
    private static final String TAG = PushService.class.getSimpleName();
    @NotNull
    private final PushDao dao = Injection.providePushDao();

    public void salvarTokenPushColaborador(@NotNull final PushColaboradorCadastro pushColaborador,
                                           @NotNull final String userToken) {
        try {
            dao.salvarTokenPushColaborador(pushColaborador, TokenCleaner.getOnlyToken(userToken));
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao salvar o token de push para o colaborador." +
                    "Colaborador: %d" +
                    "Token Prolog: %s" +
                    "Token push: %s",
                    pushColaborador.getCodColaborador(),
                    userToken,
                    pushColaborador.getTokenPushFirebase()), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao salvar token de push para o colaborador");
        }
    }
}
