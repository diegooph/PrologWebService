package br.com.zalf.prolog.webservice.messaging.email._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class EmailSender {
    @NotNull
    private final String email;
    @NotNull
    private final String name;

    private EmailSender(@NotNull final String email,
                        @NotNull final String name) {
        this.email = email;
        this.name = name;
    }

    @NotNull
    public static EmailSender of(@NotNull final String email,
                                 @NotNull final String name) {
        return new EmailSender(email, name);
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
