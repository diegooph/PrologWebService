package br.com.zalf.prolog.webservice.messaging.send;

import br.com.zalf.prolog.webservice.commons.util.Log;
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
                        @NotNull final PushMessage pushMessage) {
        Log.d(TAG, String.format("Enviando mensagem push para %d destinatário(s): %s", destinations.size(), pushMessage));

        new FirebasePushMessageWorker(destinations, pushMessage).execute();
    }
}
