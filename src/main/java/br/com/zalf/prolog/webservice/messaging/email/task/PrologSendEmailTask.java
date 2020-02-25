package br.com.zalf.prolog.webservice.messaging.email.task;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailTemplateMessage;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologSendEmailTask {

    public PrologSendEmailTask() {

    }

    @NotNull
    public EmailRequestResponseHolder deliverTemplate(@NotNull final List<String> emailsToSend,
                                                      @NotNull final EmailTemplateMessage templateMessage)
            throws MailjetSocketTimeoutException, MailjetException {
        final ClientOptions options = new ClientOptions("v3.1");
        final MailjetClient client = new MailjetClient(
                EnvironmentHelper.MAILJET_APIKEY_PUBLIC,
                EnvironmentHelper.MAILJET_APIKEY_PRIVATE,
                options);
        final JSONObject messageProperties = getMessageProperties(emailsToSend, templateMessage);
        final MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(messageProperties));
        final MailjetResponse response = client.post(request);
        return new EmailRequestResponseHolder(request, response);
    }

    @NotNull
    private JSONObject getMessageProperties(@NotNull final List<String> destinations,
                                            @NotNull final EmailTemplateMessage templateMessage) {
        final JSONObject properties = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject()
                        .put("Email", templateMessage.getSender().getEmail())
                        .put("Name", templateMessage.getSender().getName()))
                .put(Emailv31.Message.TEMPLATEID, templateMessage.getEmailTemplate().getTemplateId())
                .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                .put(Emailv31.Message.SUBJECT, templateMessage.getEmailSubject());

        // Adiciona os destinatários.
        final JSONArray destinationsJson = new JSONArray();
        for (final String email : destinations) {
            destinationsJson.put(new JSONObject().put("Email", email));
        }
        properties.put(Emailv31.Message.TO, destinationsJson);

        // Adiciona as variáveis do template.
        if (templateMessage.getTemplateVariables() != null) {
            properties.put(Emailv31.Message.VARIABLES, new JSONObject(templateMessage.getTemplateVariables()));
        }

        return properties;
    }
}
