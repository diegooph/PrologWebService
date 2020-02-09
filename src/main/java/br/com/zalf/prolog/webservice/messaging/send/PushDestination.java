package br.com.zalf.prolog.webservice.messaging.send;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PushDestination {
    @NotNull
    String getTokenPushFirebase();
    @NotNull
    String getUserIdAssociatedWithToken();
}