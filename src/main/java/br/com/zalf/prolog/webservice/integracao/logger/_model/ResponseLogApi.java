package br.com.zalf.prolog.webservice.integracao.logger._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 18/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ResponseLogApi {
    @Nullable
    private final Map<String, String> headers;
    @Nullable
    private final String annotations;
    @Nullable
    private final String entityType;
    private final boolean isError;
    private final int statusCode;
    @Nullable
    private final String body;
    @Nullable
    private final String errorBody;

    public ResponseLogApi(@Nullable final Map<String, String> headers,
                          @Nullable final String annotations,
                          @Nullable final String entityType,
                          final boolean isError,
                          final int statusCode,
                          @Nullable final String body,
                          @Nullable final String errorBody) {
        this.headers = headers;
        this.annotations = annotations;
        this.entityType = entityType;
        this.isError = isError;
        this.statusCode = statusCode;
        this.body = body;
        this.errorBody = errorBody;
    }

    @NotNull
    public static String toJson(@NotNull final ResponseLogApi responseLog) {
        return GsonUtils.getGson().toJson(responseLog);
    }

    @Nullable
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Nullable
    public String getAnnotations() {
        return annotations;
    }

    @Nullable
    public String getEntityType() {
        return entityType;
    }

    public boolean isError() {
        return isError;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    @Nullable
    public String getErrorBody() {
        return errorBody;
    }
}
