package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 08/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface AuthenticatorApi {
    void validade(@NotNull final String value,
                  @NotNull final String tag);
}