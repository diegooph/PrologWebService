package br.com.zalf.prolog.webservice.messaging.email;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologEmailApi {
    @NotNull
    private static final String TAG = PrologEmailApi.class.getSimpleName();

    public PrologEmailApi() {
    }

    public void deliverTemplate(@NotNull final List<String> emailsToSend,
                                @NotNull final MessageScope messageScope,
                                @NotNull final EmailTemplateMessage templateMessage) {
        Log.d(TAG, String.format("Enviando e-mail para %d destinatário(s): %s", emailsToSend.size(), templateMessage));
        new PrologEmailWorker(emailsToSend, messageScope, templateMessage).execute();
    }
}
