package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.messaging.push._model.PushColaboradorCadastro;
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

    public void salvarTokenPushColaborador(@NotNull final String userToken,
                                           @NotNull final PushColaboradorCadastro pushColaborador) {
        try {
            dao.salvarTokenPushColaborador(TokenCleaner.getOnlyToken(userToken), pushColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao salvar o token de push para o colaborador.\n" +
                            "Colaborador: %d\n" +
                            "Token Prolog: %s\n" +
                            "Token push: %s",
                    pushColaborador.getCodColaborador(),
                    userToken,
                    pushColaborador.getTokenPushFirebase()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao salvar token de push para o colaborador");
        }
    }
}
