package br.com.zalf.prolog.webservice.messaging.send.task;

import br.com.zalf.prolog.webservice.messaging.send.PushDestination;
import br.com.zalf.prolog.webservice.messaging.send.PushMessage;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebaseSendMulticastTask {

    public FirebaseSendMulticastTask() {

    }

    @NotNull
    public BatchResponse deliverMulticast(@NotNull final List<PushDestination> destinations,
                                          @NotNull final PushMessage pushMessage) throws FirebaseMessagingException {
        // These registration tokens come from the client FCM SDKs.
        final List<String> registrationTokens = destinations
                .stream()
                .map(PushDestination::getTokenPushFirebase)
                .collect(Collectors.toList());

        final MulticastMessage message = MulticastMessage
                .builder()
                .putAllData(pushMessage.getData())
                .addAllTokens(registrationTokens)
                .build();
        return FirebaseMessaging.getInstance().sendMulticast(message);
    }
}
