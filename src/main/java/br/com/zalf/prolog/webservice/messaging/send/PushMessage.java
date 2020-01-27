package br.com.zalf.prolog.webservice.messaging.send;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PushMessage {
    @NotNull
    private final String title;
    @NotNull
    private final String body;

    public PushMessage(@NotNull final String title,
                       @NotNull final String body) {
        this.title = title;
        this.body = body;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public String getBody() {
        return body;
    }
}
