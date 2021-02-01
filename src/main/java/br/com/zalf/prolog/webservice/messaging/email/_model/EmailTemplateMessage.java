package br.com.zalf.prolog.webservice.messaging.email._model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AllArgsConstructor
@Getter
public final class EmailTemplateMessage {
    @NotNull
    private final EmailTemplate emailTemplate;
    @NotNull
    private final EmailSender sender;
    @NotNull
    private final String emailSubject;
    @Nullable
    private final Map<String, String> templateVariables;

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
