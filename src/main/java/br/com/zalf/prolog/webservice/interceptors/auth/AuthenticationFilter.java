package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.Authenticator;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.AuthenticatorFactory;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public final class AuthenticationFilter implements ContainerRequestFilter {
    private static final String TAG = AuthenticationFilter.class.getSimpleName();

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        // Get the HTTP Authorization header from the request.
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        Log.d(TAG, "AuthorizationHeader: " + authorizationHeader);
        // Check if the HTTP Authorization header is present and formatted correctly.
        if (authorizationHeader == null) {
            throw new NotAuthorizedException("Authorization header must be provided!");
        }

        final AuthType authType;
        if (authorizationHeader.startsWith("Basic ")) {
            authType = AuthType.BASIC;
        } else if (authorizationHeader.startsWith("Bearer ")) {
            authType = AuthType.BEARER;
        } else {
            throw new NotAuthorizedException("Authorization header must be provided!");
        }

        final String value =  authorizationHeader.substring(authType.value().length()).trim();
        final Authenticator authenticator = AuthenticatorFactory.createAuthenticator(
                authType,
                new AutenticacaoService());

        final Method resourceMethod = resourceInfo.getResourceMethod();
        final Secured methodAnnot = resourceMethod.getAnnotation(Secured.class);
        if (methodAnnot != null) {
            ensureCorrectAuthType(methodAnnot, authType);
            final ColaboradorAutenticado colaboradorAutenticado = authenticator.validate(
                    value,
                    methodAnnot.permissions(),
                    methodAnnot.needsToHaveAllPermissions(),
                    methodAnnot.considerOnlyActiveUsers());
            requestContext.setSecurityContext(new PrologSecurityContext(colaboradorAutenticado));
            // Retornamos agora para impedir a verificação por classe. A por método tem prioridade, e, se existir,
            // apenas ela deve ser considerada.
            return;
        }

        final Class<?> resourceClass = resourceInfo.getResourceClass();
        final Secured classAnnot = resourceClass.getAnnotation(Secured.class);
        if (classAnnot != null) {
            ensureCorrectAuthType(classAnnot, authType);
            final ColaboradorAutenticado colaboradorAutenticado = authenticator.validate(
                    value,
                    classAnnot.permissions(),
                    classAnnot.needsToHaveAllPermissions(),
                    classAnnot.considerOnlyActiveUsers());
            requestContext.setSecurityContext(new PrologSecurityContext(colaboradorAutenticado));
        }
    }

    private void ensureCorrectAuthType(final Secured methodAnnot, final AuthType headerAuthType) {
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