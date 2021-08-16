package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.container.ContainerRequestContext;

public final class AuthorizatorFactory {

    private AuthorizatorFactory() {
        throw new IllegalStateException(AuthorizatorFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static PrologAuthorizer createAuthorizator(@NotNull final ContainerRequestContext requestContext,
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