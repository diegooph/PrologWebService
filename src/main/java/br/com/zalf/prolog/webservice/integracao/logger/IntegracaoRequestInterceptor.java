package br.com.zalf.prolog.webservice.integracao.logger;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@LogIntegracaoRequest
@Provider
public final class IntegracaoRequestInterceptor implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        if (requestContext == null)
            return;

        try {
            if (isJson(requestContext)) {
                final String tokenRequisicao = getTokenRequisicao(requestContext);
                final LogRequisicao logRequisicao = createLogRequisicao(requestContext);
                if (!logRequisicao.isEmpty()) {
                    Injection.provideLogDao().insertRequestLog(tokenRequisicao, logRequisicao);
                }
            }
        } catch (Throwable throwable) {
            // TODO - :shushing_face: Não aconteceu nada!
        }
    }

    @NotNull
    private String getTokenRequisicao(@NotNull final ContainerRequestContext requestContext) {
        final String tokenHeader = requestContext.getHeaderString(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO);
        if (tokenHeader == null) {
            throw new IllegalStateException("Não é possível ler o token da requisição");
        }
        return tokenHeader.trim();
    }

    @NotNull
    private LogRequisicao createLogRequisicao(
            @NotNull final ContainerRequestContext requestContext) throws IOException {
        final LogRequisicao logRequisicao = new LogRequisicao();
        logRequisicao.setClassResource(getFullClassDescription());
        logRequisicao.setMethodResource(getFullMethodDescription());
        logRequisicao.setHttpMethod(requestContext.getMethod());
        logRequisicao.setUrlAcesso(getFullUrlAcesso(requestContext));
        logRequisicao.setHeaders(getFullHeaders(requestContext));
        logRequisicao.setParameters(getFullParameters(requestContext));
        logRequisicao.setBodyRequest(readBody(requestContext));
        logRequisicao.setDataHoraRequisicao(LocalDateTime.now());
        return logRequisicao;
    }

    @Nullable
    private String getFullParameters(@NotNull final ContainerRequestContext requestContext) {
        if (requestContext.getUriInfo() == null
                || (requestContext.getUriInfo().getPathParameters() == null
                && requestContext.getUriInfo().getQueryParameters() == null))
            return null;

        final String pathParameters = requestContext.getUriInfo().getPathParameters().toString();
        final String queryParameters = requestContext.getUriInfo().getQueryParameters().toString();
        final String pathParams = pathParameters.isEmpty() || pathParameters.equals("{}") ? "" : pathParameters;
        final String queryParams = queryParameters.isEmpty() || queryParameters.equals("{}") ? "" : queryParameters;
        return pathParams + queryParams;
    }

    @Nullable
    private String getFullHeaders(@NotNull final ContainerRequestContext requestContext) {
        if (requestContext.getHeaders() == null)
            return null;

        return requestContext.getHeaders().toString();
    }

    @Nullable
    private String getFullUrlAcesso(@NotNull final ContainerRequestContext requestContext) {
        if (requestContext.getUriInfo() == null || requestContext.getUriInfo().getAbsolutePath() == null)
            return null;

        return requestContext.getUriInfo().getAbsolutePath().toString();
    }

    @Nullable
    private String getFullClassDescription() {
        if (resourceInfo == null || resourceInfo.getResourceClass() == null)
            return null;

        return resourceInfo.getResourceClass().getName();
    }

    @Nullable
    private String getFullMethodDescription() {
        if (resourceInfo == null || resourceInfo.getResourceMethod() == null)
            return null;
        final String returnType = resourceInfo.getResourceMethod().getGenericReturnType().getTypeName();
        final String signature = resourceInfo.getResourceMethod().getName();
        final String parameters = Arrays.toString(resourceInfo.getResourceMethod().getGenericParameterTypes());
        return returnType.concat(" ").concat(signature).concat("(").concat(parameters).concat(");");
    }

    @NotNull
    private String readBody(@NotNull final ContainerRequestContext requestContext) throws IOException {
        final String body = IOUtils.toString(requestContext.getEntityStream(), StandardCharsets.UTF_8);
        final InputStream in = IOUtils.toInputStream(body, StandardCharsets.UTF_8);
        requestContext.setEntityStream(in);
        return body;
    }

    private boolean isJson(@NotNull final ContainerRequestContext requestContext) {
        if (requestContext.getMethod().equals(HttpMethod.GET)) {
            return true;
        }
        return requestContext.getMediaType().toString().contains("application/json");
    }
}
