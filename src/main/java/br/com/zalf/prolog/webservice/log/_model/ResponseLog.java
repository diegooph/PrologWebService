package br.com.zalf.prolog.webservice.log._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 18/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ResponseLog {
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

    public ResponseLog(@Nullable final Map<String, String> headers,
                       @Nullable final String annotations,
                       @Nullable final String entityType,
                       final boolean isError,
                       final int statusCode,
                       @Nullable final String body) {
        this.headers = headers;
        this.annotations = annotations;
        this.entityType = entityType;
        this.isError = isError;
        this.statusCode = statusCode;
        this.body = isError ? null : body;
        this.errorBody = isError ? body : null;
    }

    @NotNull
    public static ResponseLog errorLog(@NotNull final Throwable t) {
        return new ResponseLog(
                null,
                null,
                null,
                true,
                500,
                "HTTP FAILED - " + ExceptionUtils.getStackTrace(t));
    }

    @NotNull
    public String toJson() {
        return GsonUtils.getGson().toJson(this);
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
