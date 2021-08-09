package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.container.ContainerRequestContext;

public final class AuthenticatorFactory {

    private AuthenticatorFactory() {
        throw new IllegalStateException(AuthenticatorFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PrologAuthenticator createAuthenticator(@NotNull final ContainerRequestContext requestContext,
                                                          @NotNull final Secured secured,
                                                          @NotNull final String authorizationHeader,
                                                          @NotNull final AuthType authType) {
        switch (authType) {
            case BEARER:
                return new BearerAuthenticator(requestContext, secured, authorizationHeader);
            case API:
                return new ApiAuthenticator(requestContext, secured, authorizationHeader);
            default:
                throw new IllegalArgumentException("No implementation available for authType: " + authType);
        }
    }
}