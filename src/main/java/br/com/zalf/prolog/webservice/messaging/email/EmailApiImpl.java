package br.com.zalf.prolog.webservice.messaging.email;

import br.com.zalf.prolog.webservice.messaging.email._model.EmailReceiver;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created on 2021-01-29
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class EmailApiImpl implements EmailApi {

    private final MailjetClient client;

    @Autowired
    public EmailApiImpl(final MailjetClient client) {
        this.client = client;
    }

    @Override
    @NotNull
    public EmailRequestResponseHolder sendMessage(final @NotNull Set<EmailReceiver> receivers,
                                                           final @NotNull EmailTemplateMessage template)
            throws MailjetException {
        final TransactionalEmail message = getMessage(receivers, template);
        final SendEmailsRequest request = getRequest(message);
        final SendEmailsResponse response = getResponse(request);
        return getHolder(request, response);
    }

    private EmailRequestResponseHolder getHolder(final SendEmailsRequest request, final SendEmailsResponse response) {
        return new EmailRequestResponseHolder(request, response);
    }

    @NotNull
    private SendEmailsRequest getRequest(@NotNull final TransactionalEmail message) {
        return SendEmailsRequest.builder()
                .message(message)
                .build();
    }

    @NotNull
    private SendEmailsResponse getResponse(@NotNull final SendEmailsRequest request) throws MailjetException {
        return request.sendWith(this.client);
    }

    @NotNull
    private TransactionalEmail getMessage(@NotNull final Set<EmailReceiver> receivers,
                                          @NotNull final EmailTemplateMessage template) {
        return TransactionalEmail.builder()
                .from(template.getSender())
                .to(receivers)
                .subject(template.getEmailSubject())
                .templateID(template.getEmailTemplate().getTemplateId())
                .templateLanguage(true)
                .variables(template.getTemplateVariables())
               .build();
    }
}
