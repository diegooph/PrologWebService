package br.com.zalf.prolog.webservice.messaging.push._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SimplePushDestination implements PushDestination {
    @NotNull
    private final String userIdAssociatedWithToken;
    @NotNull
    private final String tokenPushFirebase;

    public SimplePushDestination(@NotNull final String userIdAssociatedWithToken,
                                 @NotNull final String tokenPushFirebase) {
        this.userIdAssociatedWithToken = userIdAssociatedWithToken;
        this.tokenPushFirebase = tokenPushFirebase;
    }

    @NotNull
    @Override
    public String getTokenPushFirebase() {
        return tokenPushFirebase;
    }

    @NotNull
    @Override
    public String getUserIdAssociatedWithToken() {
        return userIdAssociatedWithToken;
    }
}
