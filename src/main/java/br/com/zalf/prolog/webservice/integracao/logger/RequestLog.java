package br.com.zalf.prolog.webservice.integracao.logger;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 18/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RequestLog {
    @Nullable
    private final Map<String, String> headers;
    @Nullable
    private final String path;
    @Nullable
    private final String httpMethod;
    @Nullable
    private final String body;

    public RequestLog(@Nullable final Map<String, String> headers,
                      @Nullable final String path,
                      @Nullable final String httpMethod,
                      @Nullable final String body) {
        this.headers = headers;
        this.path = path;
        this.httpMethod = httpMethod;
        this.body = body;
    }

    @NotNull
    public static String toJson(@NotNull final RequestLog requestLog) {
        return GsonUtils.getGson().toJson(requestLog);
    }

    @Nullable
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    @Nullable
    public String getHttpMethod() {
        return httpMethod;
    }

    @Nullable
    public String getBody() {
        return body;
    }
}
