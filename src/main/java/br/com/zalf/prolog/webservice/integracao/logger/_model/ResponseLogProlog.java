package br.com.zalf.prolog.webservice.integracao.logger._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import lombok.Data;
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
public final class ResponseLogProlog {
    private final int statusCode;
    @Nullable
    private final Map<String, List<String>> headers;
    @Nullable
    private final String body;

    @NotNull
    public static String toJson(@NotNull final ResponseLogProlog responseLog) {
        return GsonUtils.getGson().toJson(responseLog);
    }

    @NotNull
    public static ResponseLogProlog errorLog(@NotNull final Throwable t) {
        return new ResponseLogProlog(500, null, "HTTP FAILED - " + t);
    }
}
