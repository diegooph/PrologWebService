package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.errorhandling.exception.MultiAuthorizationHeadersException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
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
import java.lang.annotation.Annotation;
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
        final String bearerAuthorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        final String apiAuthorizationHeader =
                requestContext.getHeaderString(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO);
        Log.d(TAG, "AuthorizationHeader: " + bearerAuthorizationHeader);
        Log.d(TAG, "ApiAuthorizationHeader: " + apiAuthorizationHeader);

        ensureRightAuthorizationHeader(bearerAuthorizationHeader, apiAuthorizationHeader);

        if (isBearerAuthorization(bearerAuthorizationHeader)) {
            validateBearerRequest(requestContext, bearerAuthorizationHeader);
        } else if (isApiAuthorizationHeader(apiAuthorizationHeader)) {
            validateApiRequest(apiAuthorizationHeader);
        } else {
            throw new NotAuthorizedException("Unknown authorization method: " + bearerAuthorizationHeader);
        }
    }

    private void ensureRightAuthorizationHeader(@Nullable final String authorizationHeader,
                                                @Nullable final String prologAuthorizationHeader) {
        // Check if the HTTP Authorization header is present and formatted correctly.
        if (StringUtils.isNullOrEmpty(authorizationHeader)
                && StringUtils.isNullOrEmpty(prologAuthorizationHeader)) {
            throw new NotAuthorizedException("Authorization header must be provided!");
        } else if (authorizationHeader != null && prologAuthorizationHeader != null) {
            throw new MultiAuthorizationHeadersException("Multiple authorization headers!");
        }
    }

    private boolean isBearerAuthorization(@NotNull final String authorizationHeader) {
        return !StringUtils.isNullOrEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ");
    }

    private boolean isApiAuthorizationHeader(@Nullable final String apiAuthorizationHeader) {
        return !StringUtils.isNullOrEmpty(apiAuthorizationHeader);
    }

    private void validateApiRequest(@NotNull final String authorizationHeader) {
        final PrologAuthenticator authenticator =
                AuthenticatorFactory.createAuthenticator(AuthType.API, new BaseIntegracaoService());
        authenticator.validate(authorizationHeader, null);
    }

    private void validateBearerRequest(@NotNull final ContainerRequestContext requestContext,
                                       @NotNull final String authorizationHeader) {

        final Secured methodAnnot = getAnnotationMethod(Secured.class);
        if (methodAnnot != null) {
            applySecuredAnnotationValidation(requestContext, authorizationHeader, methodAnnot);
            // Retornamos agora para impedir a verificação por classe. A por método tem prioridade, e, se existir,
            // apenas ela deve ser considerada.
            return;
        }
        final Secured classAnnot = getAnnotationClass(Secured.class);
        if (classAnnot != null) {
            applySecuredAnnotationValidation(requestContext, authorizationHeader, classAnnot);
        }
    }

    private void applySecuredAnnotationValidation(@NotNull final ContainerRequestContext requestContext,
                                                  @NotNull final String authorizationHeader,
                                                  @Nullable final Secured secured) {
        final String token = TokenCleaner.getOnlyToken(authorizationHeader);
        final PrologAuthenticator authenticator =
                AuthenticatorFactory.createAuthenticator(AuthType.BEARER, new AutenticacaoService());
        final Optional<ColaboradorAutenticado> colaboradorAutenticado = authenticator.validate(token, secured);
        colaboradorAutenticado.ifPresent(colaborador -> injectColaboradorAutenticado(requestContext, colaborador));
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

    @Nullable
    private <T extends Annotation> T getAnnotationClass(@NotNull final Class<T> annotationClass) {
        return resourceInfo.getResourceClass().getAnnotation(annotationClass);
    }

    @Nullable
    private <T extends Annotation> T getAnnotationMethod(@NotNull final Class<T> annotationClass) {
        return resourceInfo.getResourceMethod().getAnnotation(annotationClass);
    }
}