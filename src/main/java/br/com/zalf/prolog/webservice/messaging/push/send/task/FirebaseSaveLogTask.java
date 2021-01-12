package br.com.zalf.prolog.webservice.messaging.push.send.task;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.PrologUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.push._model.*;
import com.google.common.base.Throwables;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.SendResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebaseSaveLogTask {
    private static final String TAG = FirebaseSaveLogTask.class.getSimpleName();

    public FirebaseSaveLogTask() {

    }

    public void saveToDatabase(@NotNull final Connection connection,
                               @NotNull final List<PushDestination> destinations,
                               @NotNull final MessageScope messageScope,
                               @NotNull final PushMessage pushMessage,
                               @Nullable final Throwable fatalSendException,
                               @Nullable final BatchResponse batchResponse) throws Throwable {
        logResponseToTerminalIfInDebug(destinations, batchResponse);
        final String pushMessageString = GsonUtils.getGson().toJson(pushMessage);
        final String fatalSendExceptionString = getFatalSendExceptionAsStringOrNull(fatalSendException);

        try (final PreparedStatement stmt = connection.prepareCall("{CALL MESSAGING.FUNC_PUSH_SALVA_LOG(" +
                "F_DATA_HORA_ATUAL           => ?," +
                "F_PUSH_MESSAGE_SCOPE        => ?," +
                "F_PUSH_MESSAGE_SENT         => ?," +
                "F_MESSAGE_TYPE              => ? :: MESSAGING.PUSH_MESSAGE_TYPE," +
                "F_PLATAFORM_DESTINATION     => ? :: MESSAGING.PUSH_PLATAFORM_DESTINATION," +
                "F_REQUEST_RESPONSE_FIREBASE => ?," +
                "F_FATAL_SEND_EXCEPTION      => ?)}")) {
            stmt.setObject(1, Now.getOffsetDateTimeUtc());
            stmt.setString(2, messageScope.asString());
            stmt.setObject(3, PostgresUtils.toJsonb(pushMessageString));
            // Enviamos apenas Multicast por enquanto.
            stmt.setString(4, FirebaseMessageType.MULTICAST.asString());
            // Enviamos apenas para o Android por enquanto.
            stmt.setString(5, FirebasePlataformDestination.ANDROID.asString());
            stmt.setObject(6, PostgresUtils.toJsonb(createLogRequestResponseAsJson(destinations, batchResponse)));
            bindValueOrNull(stmt, 7, fatalSendExceptionString, SqlType.TEXT);
            stmt.execute();
        }
    }

    @NotNull
    private String createLogRequestResponseAsJson(
            @NotNull final List<PushDestination> destinations,
            @Nullable final BatchResponse batchResponse) {
        final List<FirebaseLogRequestResponse> requestResponses = new ArrayList<>();

        for (int i = 0; i < destinations.size(); i++) {
            final PushDestination d = destinations.get(i);
            requestResponses.add(new FirebaseLogRequestResponse(
                    d.getTokenPushFirebase(),
                    d.getUserIdAssociatedWithToken(),
                    batchResponse != null && batchResponse.getResponses().get(i).isSuccessful(),
                    batchResponse != null ? batchResponse.getResponses().get(i).getMessageId() : null,
                    batchResponse != null && batchResponse.getResponses().get(i).getException() != null
                            ? Throwables.getStackTraceAsString(batchResponse.getResponses().get(i).getException())
                            : null));
        }

        final FirebaseLogRequestResponseHolder holder = new FirebaseLogRequestResponseHolder(
                batchResponse != null ? batchResponse.getSuccessCount() : 0,
                batchResponse != null ? batchResponse.getFailureCount() : 0,
                requestResponses);
        return GsonUtils.getGson().toJson(holder);
    }

    @Nullable
    private String getFatalSendExceptionAsStringOrNull(@Nullable final Throwable fatalSendException) {
        return fatalSendException != null ? Throwables.getStackTraceAsString(fatalSendException) : null;
    }

    private void logResponseToTerminalIfInDebug(@NotNull final List<PushDestination> destinations,
                                                @Nullable final BatchResponse batchResponse) {
        if (batchResponse == null || !PrologUtils.isDebug()) {
            return;
        }

        // These registration tokens come from the client FCM SDKs.
        final List<String> registrationTokens = destinations
                .stream()
                .map(PushDestination::getTokenPushFirebase)
                .collect(Collectors.toList());

        if (batchResponse.getFailureCount() > 0) {
            final List<SendResponse> responses = batchResponse.getResponses();
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
}
