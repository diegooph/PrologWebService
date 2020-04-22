package br.com.zalf.prolog.webservice.messaging.push.send;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.messaging.push._model.PushDestination;
import br.com.zalf.prolog.webservice.messaging.push._model.PushMessage;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.push.send.task.FirebaseSaveLogTask;
import br.com.zalf.prolog.webservice.messaging.push.send.task.FirebaseSendMulticastTask;
import com.google.firebase.messaging.BatchResponse;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 2020-01-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class FirebasePushMessageWorker {
    private static final String TAG = FirebasePushMessageWorker.class.getSimpleName();
    @NotNull
    private final List<PushDestination> destinations;
    @NotNull
    private final MessageScope messageScope;
    @NotNull
    private final PushMessage pushMessage;

    FirebasePushMessageWorker(@NotNull final List<PushDestination> destinations,
                              @NotNull final MessageScope messageScope,
                              @NotNull final PushMessage pushMessage) {
        this.destinations = destinations;
        this.messageScope = messageScope;
        this.pushMessage = pushMessage;
    }

    public void execute() {
        Throwable sendException = null;
        BatchResponse batchResponse = null;
        try {
            final FirebaseSendMulticastTask multicastTask = new FirebaseSendMulticastTask();
            batchResponse = multicastTask.deliverMulticast(destinations, pushMessage);
        } catch (final Throwable throwable) {
            sendException = throwable;
            final String errorMessage = String.format(
                    "Erro fatal ao enviar mensagens pelo Firebase! Nada foi enviado.\nMensagem: %s",
                    pushMessage.getFullMessageAsString());
            Log.e(TAG, errorMessage, throwable);
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            final FirebaseSaveLogTask saveLogTask = new FirebaseSaveLogTask();
            saveLogTask.saveToDatabase(
                    connection,
                    destinations,
                    messageScope,
                    pushMessage,
                    sendException,
                    batchResponse);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao salvar logs do Firebase", throwable);
        } finally {
            DatabaseConnection.close(connection);
        }
    }
}
