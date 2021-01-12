package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.MultiAuthorizationHeadersException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.interceptors.ApiExposed;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.Authenticator;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.AuthenticatorApi;
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
import java.util.Arrays;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public final class AuthenticationFilter implements ContainerRequestFilter {
    @NotNull
    private static final String TAG = AuthenticationFilter.class.getSimpleName();
    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        // Get the HTTP Authorization header from the request.
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        final String prologAuthorizationHeader =
                requestContext.getHeaderString(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO);

        Log.d(TAG, "AuthorizationHeader: " + authorizationHeader);
        Log.d(TAG, "PrologAuthorizationHeader: " + prologAuthorizationHeader);
        // Check if the HTTP Authorization header is present and formatted correctly.
        if ((StringUtils.isNullOrEmpty(authorizationHeader)) &&
                (StringUtils.isNullOrEmpty(prologAuthorizationHeader))) {
            throw new NotAuthorizedException("Authorization header must be provided!");
        } else if (authorizationHeader != null && prologAuthorizationHeader != null) {
            throw new MultiAuthorizationHeadersException("Multiples authorizations headers!");
        }

        final AuthType authType;
        if (!StringUtils.isNullOrEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            authType = AuthType.BEARER;
        } else if (!StringUtils.isNullOrEmpty(authorizationHeader) && authorizationHeader.startsWith("Basic ")) {
            authType = AuthType.BASIC;
        } else if (!StringUtils.isNullOrEmpty(prologAuthorizationHeader)) {
            authType = AuthType.API;
            final AuthenticatorApi authenticatorApi =
                    AuthenticatorFactory.createAuthenticatorApi(authType, new BaseIntegracaoService());
            final Method resourceMethodApi = resourceInfo.getResourceMethod();
            final ApiExposed methodAnnotApi = resourceMethodApi.getAnnotation(ApiExposed.class);
            if (methodAnnotApi != null) {
                ensureCorrectAuthType(methodAnnotApi.authTypes(), authType);
                authenticatorApi.validade(prologAuthorizationHeader, TAG);
                // Retornamos agora para impedir as demais verificações. A por integração tem prioridade, e, se existir,
                // apenas ela deve ser considerada.
                return;
            }
            final Class<?> resourceClass = resourceInfo.getResourceClass();
            final ApiExposed classAnnotApi = resourceClass.getAnnotation(ApiExposed.class);
            if (classAnnotApi != null) {
                ensureCorrectAuthType(classAnnotApi.authTypes(), authType);
                authenticatorApi.validade(prologAuthorizationHeader, TAG);
                return;
            }
        } else {
            throw new NotAuthorizedException("Authorization header must be provided!");
        }

        final String value = authorizationHeader.substring(authType.value().length()).trim();
        final Authenticator authenticator =
                AuthenticatorFactory.createAuthenticator(authType, new AutenticacaoService());
        final Method resourceMethod = resourceInfo.getResourceMethod();
        final Secured methodAnnot = resourceMethod.getAnnotation(Secured.class);
        if (methodAnnot != null) {
            ensureCorrectAuthType(methodAnnot.authTypes(), authType);
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
            ensureCorrectAuthType(classAnnot.authTypes(), authType);
            final ColaboradorAutenticado colaboradorAutenticado = authenticator.validate(
                    value,
                    classAnnot.permissions(),
                    classAnnot.needsToHaveAllPermissions(),
                    classAnnot.considerOnlyActiveUsers());
            requestContext.setSecurityContext(new PrologSecurityContext(colaboradorAutenticado));
        }
    }

    private void ensureCorrectAuthType(@NotNull final AuthType[] permitedAuthTypes,
                                       @NotNull final AuthType headerAuthType) {
        if (Arrays.stream(permitedAuthTypes).anyMatch(authType -> authType == headerAuthType)) {
            return;
        }
        throw new NotAuthorizedException("Authorization method not allowed for this resource: " + headerAuthType);
    }
}