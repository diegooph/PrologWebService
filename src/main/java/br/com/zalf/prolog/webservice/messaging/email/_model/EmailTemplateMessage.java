package br.com.zalf.prolog.webservice.messaging.email._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EmailTemplateMessage {
    @NotNull
    private final EmailTemplate emailTemplate;
    @NotNull
    private final EmailSender sender;
    @NotNull
    private final String emailSubject;
    @Nullable
    private final Map<String, String> templateVariables;

    public EmailTemplateMessage(@NotNull final EmailTemplate emailTemplate,
                                @NotNull final EmailSender sender,
                                @NotNull final String emailSubject,
                                @Nullable final Map<String, String> templateVariables) {
        this.emailTemplate = emailTemplate;
        this.sender = sender;
        this.emailSubject = emailSubject;
        this.templateVariables = templateVariables;
    }

    @NotNull
    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    @NotNull
    public EmailSender getSender() {
        return sender;
    }

    @NotNull
    public String getEmailSubject() {
        return emailSubject;
    }

    @Nullable
    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }

    @Override
    public String toString() {
        return "EmailTemplateMessage{" +
                "emailTemplate=" + emailTemplate +
                ", sender=" + sender +
                ", emailSubject='" + emailSubject + '\'' +
                ", templateVariables=" + templateVariables +
                '}';
    }
}
