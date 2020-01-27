package br.com.zalf.prolog.webservice.push.send;

import br.com.zalf.prolog.webservice.commons.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebasePushMessageSender {
    private static final String TAG = FirebasePushMessageSender.class.getSimpleName();

    public FirebasePushMessageSender() {

    }

    public void deliver(@NotNull final PushDestination destination,
                        @NotNull final PushMessage pushMessage) throws IOException, FirebaseMessagingException {
        initializeFirebase();

        // This registration token comes from the client FCM SDKs.
        final String registrationToken = destination.provideTokenPushFirebase();

        // See documentation on defining a message payload.
        final Message message = Message.builder()
                .setNotification(Notification
                        .builder()
                        .setTitle(pushMessage.getTitle())
                        .setBody(pushMessage.getBody())
                        .build())
                .setToken(registrationToken)
                .build();

        // Send a message to the device corresponding to the provided
        // registration token.
        final String response = FirebaseMessaging.getInstance().send(message);
        System.out.println(response);
    }

    public void deliver(@NotNull final List<PushDestination> destinations,
                        @NotNull final PushMessage pushMessage) throws IOException, FirebaseMessagingException {
        Log.d(TAG, String.format("Enviando mensagem push para %d destinatários", destinations.size()));
        initializeFirebase();

        // These registration tokens come from the client FCM SDKs.
        final List<String> registrationTokens = destinations
                .stream()
                .map(PushDestination::provideTokenPushFirebase)
                .collect(Collectors.toList());

        final MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification
                        .builder()
                        .setTitle(pushMessage.getTitle())
                        .setBody(pushMessage.getBody())
                        .build())
                .addAllTokens(registrationTokens)
                .build();
        final BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
        if (response.getFailureCount() > 0) {
            final List<SendResponse> responses = response.getResponses();
            final List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    // The order of responses corresponds to the order of the registration tokens.
                    failedTokens.add(registrationTokens.get(i));
                }
            }

            Log.d(TAG, "List of tokens that caused failures: " + failedTokens);
        } else {
            Log.d(TAG, "Todos as mensagens foram enviadas com sucesso");
        }
    }

    private void initializeFirebase() throws IOException {
        final FileInputStream serviceAccount =
                new FileInputStream("/Users/luiz/Downloads/prolog-debug-firebase-adminsdk.json");

        final FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://prolog-debug.firebaseio.com")
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
