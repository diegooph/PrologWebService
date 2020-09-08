package br.com.zalf.prolog.webservice.integracao.logger._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created on 2020-08-24
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ResponseLogProlog implements RequestResponseLog {
    private final int statusCode;
    @Nullable
    private final Map<String, List<String>> headers;
    @Nullable
    private final String body;

    @NotNull
    @Override
    public String toJson() {
        return GsonUtils.getGson().toJson(this);
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @NotNull
    public static ResponseLogProlog errorLog(@NotNull final Throwable t) {
        return new ResponseLogProlog(
                500,
                null,
                "HTTP FAILED - " + ExceptionUtils.getStackTrace(t));
    }
}
