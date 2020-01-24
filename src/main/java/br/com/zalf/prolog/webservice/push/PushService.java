package br.com.zalf.prolog.webservice.push;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.push._model.PushColaboradorCadastro;
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

    public void salvarTokenPushColaborador(@NotNull final PushColaboradorCadastro pushColaborador) {
        try {
            dao.salvarTokenPushColaborador(pushColaborador);
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao salvar o token de push para o colaborador." +
                    "Colaborador: %d" +
                    "Token: %s", pushColaborador.getCodColaborador(), pushColaborador.getTokenPushFirebase()), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao salvar token de push para o colaborador");
        }
    }
}
