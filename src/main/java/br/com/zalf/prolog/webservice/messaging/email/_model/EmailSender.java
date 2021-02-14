package br.com.zalf.prolog.webservice.messaging.email._model;

import com.mailjet.client.transactional.SendContact;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EmailSender extends SendContact {

    private EmailSender(@NotNull final String email, @NotNull final String name) {
        super(email, name);
    }
    private EmailSender(@NotNull final String email) {
        super(email);
    }

    public static EmailSender of(@NotNull final String email,
                                 @NotNull final String name) {
        return new EmailSender(email, name);
    }

    public static EmailSender of(@NotNull final String email) {
        return new EmailSender(email);
    }
}
