package br.com.zalf.prolog.webservice.messaging.email;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailReceiver;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import br.com.zalf.prolog.webservice.messaging.email.task.PrologEmailSaveLogTask;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
public final class PrologEmailApi {

    private static final String TAG = PrologEmailApi.class.getSimpleName();

    private final EmailApi emailApi;
    private final PrologEmailSaveLogTask logTask;

    @Autowired
    public PrologEmailApi(@NotNull final EmailApi emailApi, @NotNull final PrologEmailSaveLogTask logTask) {
        this.emailApi = emailApi;
        this.logTask = logTask;
    }

    public void deliverTemplate(@NotNull final Set<EmailReceiver> receivers,
                                @NotNull final MessageScope messageScope,
                                @NotNull final EmailTemplateMessage template) {
        Throwable emailApiException = null;
        EmailRequestResponseHolder holder = null;
        try {
            Log.d(TAG, String.format("Enviando e-mail para %d destinatário(s): %s", receivers.size(), template));
            holder = this.emailApi.sendMessage(receivers, template);
        } catch (final Throwable t) {
            emailApiException = t;
            Log.e(TAG, "Erro fatal ao enviar e-mails via API! Nada foi enviado.", t);
        }
        logTask.saveToDatabase(messageScope, holder, emailApiException);
    }
}
