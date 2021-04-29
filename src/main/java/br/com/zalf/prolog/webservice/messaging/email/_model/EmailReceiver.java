package br.com.zalf.prolog.webservice.messaging.email._model;

import com.mailjet.client.transactional.SendContact;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-01-29
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public final class EmailReceiver extends SendContact {

    private EmailReceiver(final String email, final String name) {
        super(email, name);
    }

    private EmailReceiver(final String email) {
        super(email);
    }

    public static EmailReceiver of(@NotNull final String email, @NotNull final String name) {
        return new EmailReceiver(email, name);
    }

    public static EmailReceiver of(@NotNull final String email) {
        return new EmailReceiver(email);
    }
}
