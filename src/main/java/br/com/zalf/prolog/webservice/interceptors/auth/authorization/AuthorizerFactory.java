package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.container.ContainerRequestContext;

public final class AuthorizerFactory {

    private AuthorizerFactory() {
        throw new IllegalStateException(AuthorizerFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PrologAuthorizer createAuthorizer(@NotNull final ContainerRequestContext requestContext,
                                                    @NotNull final Secured secured,
                                                    @NotNull final AuthMethod authMethod) {
        switch (authMethod.getAuthType()) {
            case BEARER:
                return new BearerAuthorizer(requestContext, secured, authMethod);
            case API:
                return new ApiAuthorizer(requestContext, secured, authMethod);
            default:
                throw new IllegalArgumentException("No implementation available for authType: "
                                                           + authMethod.getAuthType());
        }
    }
}