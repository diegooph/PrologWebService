package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.Authenticator;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.AuthenticatorFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public final class AuthenticationFilter implements ContainerRequestFilter {
    private static final String TAG = AuthenticationFilter.class.getSimpleName();

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Get the HTTP Authorization header from the request
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        Log.d(TAG, "AuthorizationHeader: " + authorizationHeader);
        // Check if the HTTP Authorization header is present and formatted correctly.
        if (authorizationHeader == null) {
            throw new NotAuthorizedException("Authorization header must be provided!");
        }

        AuthType authType;
        if (authorizationHeader.startsWith("Basic ")) {
            authType = AuthType.BASIC;
        } else if (authorizationHeader.startsWith("Bearer ")) {
            authType = AuthType.BEARER;
        } else if (authorizationHeader.startsWith("Token ")) {
            authType = AuthType.TOKEN;
        } else {
            throw new NotAuthorizedException("Authorization header must be provided!");
        }

        final String value = authorizationHeader.substring(authType.value().length()).trim();
        final Authenticator authenticator = AuthenticatorFactory
                .createAuthenticator(authType, new AutenticacaoService());
        final Method resourceMethod = resourceInfo.getResourceMethod();
        final Secured methodAnnot = resourceMethod.getAnnotation(Secured.class);
        if (methodAnnot != null) {
            ensureCorrectAuthType(methodAnnot, authType);
            authenticator.validate(
                    value,
                    methodAnnot.permissions(),
                    methodAnnot.needsToHaveAllPermissions(),
                    methodAnnot.considerOnlyActiveUsers());
        }

        final Class<?> resourceClass = resourceInfo.getResourceClass();
        final Secured classAnnot = resourceClass.getAnnotation(Secured.class);
        if (classAnnot != null) {
            ensureCorrectAuthType(classAnnot, authType);
            authenticator.validate(
                    value,
                    classAnnot.permissions(),
                    classAnnot.needsToHaveAllPermissions(),
                    classAnnot.considerOnlyActiveUsers());
        }
    }

    private void ensureCorrectAuthType(@NotNull final Secured methodAnnot, @NotNull final AuthType headerAuthType) {
        final AuthType[] permitedAuthTypes = methodAnnot.authTypes();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < permitedAuthTypes.length; i++) {
            final AuthType authType = permitedAuthTypes[i];
            if (authType == headerAuthType) {
                return;
            }
        }
        throw new NotAuthorizedException("Authorization method not allowed for this resource: " + headerAuthType);
    }
}