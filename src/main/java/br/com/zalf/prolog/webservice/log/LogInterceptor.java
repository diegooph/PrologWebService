package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.commons.KeyCaseInsensitiveMap;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.log._model.LogLevel;
import br.com.zalf.prolog.webservice.log._model.RequestLog;
import br.com.zalf.prolog.webservice.log._model.ResponseLog;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@LogRequest
@Provider
public final class LogInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    @NotNull
    private static final String TAG = LogInterceptor.class.getSimpleName();
    @NotNull
    private static final String REQUEST_BODY_CONSTANT = RequestLog.class.getName() + ".request_object";
    private static final short FIRST_SUCCESS_STATUS = 200;
    private static final short FIRST_ERROR_STATUS = 300;
    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        if (requestContext == null) {
            return;
        }

        final LogRequest logRequest = getLogRequest();
        if (shouldLogBody(logRequest)) {
            requestContext.setProperty(REQUEST_BODY_CONSTANT, readBody(requestContext));
        }
    }

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext responseContext) {
        final LogRequest logRequest = getLogRequest();
        processRequestResponse(requestContext, responseContext, logRequest);
    }

    private void processRequestResponse(@NotNull final ContainerRequestContext requestContext,
                                        @Nullable final ContainerResponseContext responseContext,
                                        @NotNull final LogRequest logRequest) {
        try {
            final RequestLog requestLog = getRequestLog(requestContext, logRequest);
            if (requestLog == null) {
                return;
            }

            final ResponseLog responseLog = getResponseLog(responseContext, logRequest);
            new LogService().saveLogToDatabaseAsync(requestLog, responseLog);
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível extrair informações do RequestContext", t);
        }
    }

    @NotNull
    private LogRequest getLogRequest() {
        final Method methodAnnotated = resourceInfo.getResourceMethod();
        return methodAnnotated.getAnnotation(LogRequest.class);
    }

    @Nullable
    private RequestLog getRequestLog(@NotNull final ContainerRequestContext requestContext,
                                     @NotNull final LogRequest logRequest) {
        if (!isJson(requestContext)) {
            return null;
        }

        final String requestBody = (String) requestContext.getProperty(REQUEST_BODY_CONSTANT);

        return new RequestLog(
                shouldLogHeaders(logRequest) ? getHeaders(requestContext.getHeaders()) : null,
                getPath(requestContext),
                requestContext.getMethod(),
                shouldLogBody(logRequest) ? requestBody : null);
    }

    @Nullable
    private ResponseLog getResponseLog(@Nullable final ContainerResponseContext responseContext,
                                       @NotNull final LogRequest logRequest) {
        if (responseContext == null || !isJson(responseContext)) {
            return null;
        }

        final ResponseLog responseLog;
        final boolean isError = verifyIfResponseStatusIsError(responseContext.getStatus());
        responseLog = new ResponseLog(
                shouldLogHeaders(logRequest) ? getHeaders(responseContext.getHeaders()) : null,
                getMethodAnnotations(responseContext),
                getEntityType(responseContext),
                isError,
                responseContext.getStatus(),
                shouldLogBody(logRequest) ? readBody(responseContext) : null);
        return responseLog;
    }

    @Nullable
    private String getEntityType(@NotNull final ContainerResponseContext responseContext) {
        if (responseContext.getEntityType() == null) {
            return null;
        }
        return responseContext.getEntityType().getTypeName();
    }

    @Nullable
    private String getMethodAnnotations(@NotNull final ContainerResponseContext responseContext) {
        if (responseContext.getEntityAnnotations() == null) {
            return null;
        }
        return Arrays.toString(responseContext.getEntityAnnotations());
    }

    @Nullable
    private String getPath(@NotNull final ContainerRequestContext requestContext) {
        final URI requestUri = ((ContainerRequest) requestContext).getRequestUri();
        if (requestUri == null) {
            return null;
        }
        return requestUri.toString();
    }

    private boolean shouldLogHeaders(@NotNull final LogRequest logRequest) {
        return logRequest.logLevel() == LogLevel.HEADERS || logRequest.logLevel() == LogLevel.ALL;
    }

    @Nullable
    private KeyCaseInsensitiveMap<String, String> getHeaders(@Nullable final Map<String, ?> maybeHeaders) {
        if (maybeHeaders == null) {
            return null;
        }

        final KeyCaseInsensitiveMap<String, String> headers = new KeyCaseInsensitiveMap<>();
        maybeHeaders.forEach((s, objects) -> headers.put(s, objects.toString()));
        return headers;
    }

    private boolean shouldLogBody(@NotNull final LogRequest logRequest) {
        return logRequest.logLevel() == LogLevel.BODY || logRequest.logLevel() == LogLevel.ALL;
    }

    @NotNull
    private String readBody(@NotNull final ContainerResponseContext responseContext) {
        return GsonUtils.getGson().toJson(responseContext.getEntity());
    }

    @Nullable
    private String readBody(@NotNull final ContainerRequestContext requestContext) {
        try {
            final String body = IOUtils.toString(requestContext.getEntityStream(), StandardCharsets.UTF_8);
            final InputStream in = IOUtils.toInputStream(body, StandardCharsets.UTF_8);
            requestContext.setEntityStream(in);
            return body;
        } catch (final IOException io) {
            return null;
        }
    }

    private boolean isJson(@NotNull final ContainerResponseContext responseContext) {
        return responseContext.getMediaType().toString().contains("application/json");
    }

    private boolean isJson(@NotNull final ContainerRequestContext requestContext) {
        if (requestContext.getMethod().equals(HttpMethod.GET)
                || requestContext.getMethod().equals(HttpMethod.PUT)
                || requestContext.getMethod().equals(HttpMethod.POST)
                || requestContext.getMethod().equals(HttpMethod.DELETE)) {
            return true;
        }
        return requestContext.getMediaType().toString().contains("application/json");
    }

    private boolean verifyIfResponseStatusIsError(final int responseStatus) {
        return responseStatus < FIRST_SUCCESS_STATUS || responseStatus >= FIRST_ERROR_STATUS;
    }
}
