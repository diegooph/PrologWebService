package br.com.zalf.prolog.webservice.messaging.email;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import br.com.zalf.prolog.webservice.messaging.email.task.PrologEmailSaveLogTask;
import br.com.zalf.prolog.webservice.messaging.email.task.PrologSendEmailTask;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologEmailWorker {
    private static final String TAG = PrologEmailWorker.class.getSimpleName();
    @NotNull
    private final List<String> emailsToSend;
    @NotNull
    private final MessageScope messageScope;
    @NotNull
    private final EmailTemplateMessage templateMessage;

    PrologEmailWorker(@NotNull final List<String> emailsToSend,
                      @NotNull final MessageScope messageScope,
                      @NotNull final EmailTemplateMessage templateMessage) {
        this.emailsToSend = emailsToSend;
        this.messageScope = messageScope;
        this.templateMessage = templateMessage;
    }

    public void execute() {
        Throwable sendException = null;
        EmailRequestResponseHolder holder = null;
        try {
            final PrologSendEmailTask sendEmailTask = new PrologSendEmailTask();
            holder = sendEmailTask.deliverTemplate(emailsToSend, templateMessage);
        } catch (final Throwable throwable) {
            sendException = throwable;
            Log.e(TAG, "Erro fatal ao enviar e-mails via API! Nada foi enviado", throwable);
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            final PrologEmailSaveLogTask saveLogTask = new PrologEmailSaveLogTask();
            saveLogTask.saveToDatabase(
                    connection,
                    messageScope,
                    holder,
                    sendException);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao salvar logs de e-mail", throwable);
        } finally {
            DatabaseConnection.close(connection);
        }
    }
}
