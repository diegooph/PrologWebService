package br.com.zalf.prolog.webservice.push.send;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PushDestination {
    @NotNull
    private final String tokenPushFirebase;

    public PushDestination(@NotNull final String tokenPushFirebase) {
        this.tokenPushFirebase = tokenPushFirebase;
    }

    @NotNull
    public String getTokenPushFirebase() {
        return tokenPushFirebase;
    }
}