package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.errorhandling.exception.MultiAuthorizationHeadersException;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.AuthenticatorFactory;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.PrologAuthenticator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.Optional;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public final class AuthenticationFilter implements ContainerRequestFilter {
    @NotNull
    private static final String TAG = AuthenticationFilter.class.getSimpleName();
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        final String bearerAuthHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        final String apiAuthHeader = requestContext.getHeaderString(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO);
        Log.d(TAG, "AuthorizationHeader: " + bearerAuthHeader);
        Log.d(TAG, "ApiAuthorizationHeader: " + apiAuthHeader);

        ensureRightAuthorizationHeader(bearerAuthHeader, apiAuthHeader);
        final AuthType authType = getAuthTypeFromHeaders(bearerAuthHeader, apiAuthHeader);

        getSecuredAnnotation().ifPresent(secured -> {
            ensureCorrectAuthType(secured.authTypes(), authType);
            final PrologAuthenticator authenticator = AuthenticatorFactory.createAuthenticator(
                    requestContext,
                    secured,
                    authType == AuthType.BEARER ? bearerAuthHeader : apiAuthHeader,
                    authType);
            final Optional<ColaboradorAutenticado> colaboradorAutenticado = authenticator.validate();
            colaboradorAutenticado.ifPresent(colaborador -> injectColaboradorAutenticado(requestContext, colaborador));
        });
    }

    private void ensureRightAuthorizationHeader(@Nullable final String authorizationHeader,
                                                @Nullable final String prologAuthorizationHeader) {
        // Check if any HTTP Authorization header is present and formatted correctly.
        if (StringUtils.isNullOrEmpty(authorizationHeader)
                && StringUtils.isNullOrEmpty(prologAuthorizationHeader)) {
            throw new NotAuthorizedException("Authorization header must be provided!");
        } else if (authorizationHeader != null && prologAuthorizationHeader != null) {
            throw new MultiAuthorizationHeadersException("Multiple authorization headers!");
        }
    }

    @NotNull
    private AuthType getAuthTypeFromHeaders(@Nullable final String bearerAuthorizationHeader,
                                            @Nullable final String apiAuthorizationHeader) {
        final AuthType authType;
        if (isBearerAuthorization(bearerAuthorizationHeader)) {
            authType = AuthType.BEARER;
        } else if (isApiAuthorizationHeader(apiAuthorizationHeader)) {
            authType = AuthType.API;
        } else {
            throw new NotAuthorizedException("Unknown authorization method: " + bearerAuthorizationHeader);
        }
        return authType;
    }

    private boolean isBearerAuthorization(@Nullable final String authorizationHeader) {
        return !StringUtils.isNullOrEmpty(authorizationHeader)
                && authorizationHeader.startsWith("Bearer ");
    }

    private boolean isApiAuthorizationHeader(@Nullable final String authorizationHeader) {
        return !StringUtils.isNullOrEmpty(authorizationHeader)
                && authorizationHeader.startsWith(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO);
    }

    private void injectColaboradorAutenticado(@NotNull final ContainerRequestContext requestContext,
                                              @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        addColaboradorAutenticadoOnErrorReportSystem(colaboradorAutenticado);
        addColaboradorAutenticadoOnRequestScope(requestContext, colaboradorAutenticado);
    }

    private void addColaboradorAutenticadoOnErrorReportSystem(
            @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        ErrorReportSystem.addCodColaborador(colaboradorAutenticado.getCodigo());
    }

    private void addColaboradorAutenticadoOnRequestScope(@NotNull final ContainerRequestContext requestContext,
                                                         @NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        requestContext.setSecurityContext(new PrologSecurityContext(colaboradorAutenticado));
    }

    @NotNull
    private Optional<Secured> getSecuredAnnotation() {
        final Secured methodAnnotation = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (methodAnnotation != null) {
            // Priorizamos as permissões aplicadas aos métodos do que as aplicadas a nível de classe.
            return Optional.of(methodAnnotation);
        }
        return Optional.ofNullable(resourceInfo.getResourceClass().getAnnotation(Secured.class));
    }

    private void ensureCorrectAuthType(@NotNull final AuthType[] permitedAuthTypes,
                                       @NotNull final AuthType headerAuthType) {
        if (Arrays.stream(permitedAuthTypes).anyMatch(authType -> authType == headerAuthType)) {
            return;
        }
        throw new NotAuthorizedException("Authorization method not allowed for this resource: " + headerAuthType);
    }
}