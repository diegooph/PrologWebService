package br.com.zalf.prolog.webservice.push.send;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PushDestination {
    @NotNull
    String provideTokenPushFirebase();
}