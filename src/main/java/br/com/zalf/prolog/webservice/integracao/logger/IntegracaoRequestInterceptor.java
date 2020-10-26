package br.com.zalf.prolog.webservice.integracao.logger;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogCustomHeaders;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.integracao.logger._model.RequestLogApi;
import br.com.zalf.prolog.webservice.integracao.logger._model.ResponseLogApi;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@LogIntegracaoRequest
@Provider
public final class IntegracaoRequestInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    @NotNull
    private static final String TAG = IntegracaoRequestInterceptor.class.getSimpleName();
    @NotNull
    private static final String REQUEST_OBJECT = RequestLogApi.class.getName() + ".request_object";
    private static final int STATUS_OK = 200;
    private static final int STATUS_ERROR = 300;

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        if (requestContext == null) {
            return;
        }

        try {
            if (isJson(requestContext)) {
                final RequestLogApi requestLog = new RequestLogApi(
                        getHeaders(requestContext),
                        getPath(requestContext),
                        requestContext.getMethod(),
                        readBody(requestContext));
                // Setamos o nosso objeto nas propriedades do Request para recuperar no contexto do Response.
                requestContext.setProperty(REQUEST_OBJECT, requestLog);
            }
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível extrair informações do RequestContext", t);
        }
    }

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext responseContext) {
        if (requestContext == null) {
            return;
        }

        final String tokenIntegracao = getTokenIntegracao(requestContext);
        RequestLogApi requestLog = null;
        if (requestContext.getProperty(REQUEST_OBJECT) instanceof RequestLogApi) {
            requestLog = (RequestLogApi) requestContext.getProperty(REQUEST_OBJECT);
        }
        if (requestLog == null || tokenIntegracao == null) {
            // Se não há um Objeto de Request ou não temos um token, não tem necessidade de salvar uma resposta avulsa.
            return;
        }

        ResponseLogApi responseLog = null;
        if (responseContext != null) {
            try {
                if (isJson(responseContext)) {
                    final boolean isError = verifyIfResponseStatusIsError(responseContext.getStatus());
                    responseLog = new ResponseLogApi(
                            getHeaders(responseContext),
                            getAnnotations(responseContext),
                            getEntityType(responseContext),
                            isError,
                            responseContext.getStatus(),
                            isError ? null : readBody(responseContext),
                            isError ? readBody(responseContext) : null);
                }
            } catch (final Throwable t) {
                Log.e(TAG, "Não foi possível extrair informações do ResponseContext", t);
            }
        }

        try {
            Injection.provideLogDao().insertRequestResponseLogApi(tokenIntegracao, requestLog, responseLog);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir log de requisição no banco de dados", t);
        }
    }

    @Nullable
    private String getTokenIntegracao(@NotNull final ContainerRequestContext requestContext) {
        final String tokenHeader = requestContext.getHeaderString(ProLogCustomHeaders.HEADER_TOKEN_INTEGRACAO);
        return StringUtils.trimToNull(tokenHeader);
    }

    @Nullable
    private String getEntityType(@NotNull final ContainerResponseContext responseContext) {
        if (responseContext.getEntityType() == null) {
            return null;
        }
        return responseContext.getEntityType().getTypeName();
    }

    @Nullable
    private String getAnnotations(@NotNull final ContainerResponseContext responseContext) {
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

    @Nullable
    private Map<String, String> getHeaders(@NotNull final ContainerResponseContext responseContext) {
        if (responseContext.getHeaders() == null) {
            return null;
        }

        final Map<String, String> headers = new HashMap<>();
        responseContext.getHeaders().forEach((s, objects) -> headers.put(s, objects.toString()));
        return headers;
    }

    @Nullable
    private Map<String, String> getHeaders(@NotNull final ContainerRequestContext requestContext) {
        if (requestContext.getHeaders() == null) {
            return null;
        }

        final Map<String, String> headers = new HashMap<>();
        requestContext.getHeaders().forEach((s, objects) -> headers.put(s, objects.toString()));
        return headers;
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
        return responseStatus < STATUS_OK || responseStatus >= STATUS_ERROR;
    }
}
