package br.com.zalf.prolog.webservice.messaging.push.send;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.messaging.push._model.PushDestination;
import br.com.zalf.prolog.webservice.messaging.push._model.PushMessage;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebasePushMessageApi {
    private static final String TAG = FirebasePushMessageApi.class.getSimpleName();

    public FirebasePushMessageApi() {

    }

    public void deliver(@NotNull final List<PushDestination> destinations,
                        @NotNull final MessageScope messageScope,
                        @NotNull final PushMessage pushMessage) {
        Log.d(TAG, String.format("Enviando mensagem push para %d destinatário(s): %s", destinations.size(), pushMessage));

        new FirebasePushMessageWorker(destinations, messageScope, pushMessage).execute();
    }
}
