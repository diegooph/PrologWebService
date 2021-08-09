package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import org.jetbrains.annotations.NotNull;

public final class AuthenticatorFactory {

    private AuthenticatorFactory() {
        throw new IllegalStateException(AuthenticatorFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PrologAuthenticator createAuthenticator(@NotNull final AuthType authType,
                                                          @NotNull final RequestAuthenticator service) {
        switch (authType) {
            case BEARER:
                return new BearerAuthenticator(service);
            case API:
                return new ApiAuthenticator(service);
            default:
                throw new IllegalArgumentException();
        }
    }
}