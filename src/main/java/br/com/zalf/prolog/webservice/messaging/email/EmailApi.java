package br.com.zalf.prolog.webservice.messaging.email;

import br.com.zalf.prolog.webservice.messaging.email._model.EmailReceiver;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import com.mailjet.client.errors.MailjetException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Created on 2021-01-29
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface EmailApi {

    @NotNull
    EmailRequestResponseHolder sendMessage(@NotNull Set<EmailReceiver> receivers,
                                           @NotNull EmailTemplateMessage message) throws MailjetException;

}
