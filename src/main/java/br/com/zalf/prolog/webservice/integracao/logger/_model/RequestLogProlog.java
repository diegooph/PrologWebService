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
public final class RequestLogProlog {
    @NotNull
    private final String method;
    @NotNull
    private final String url;
    @Nullable
    private final String protocol;
    @Nullable
    private final Map<String, List<String>> headers;
    @Nullable
    private final String body;

    @NotNull
    public static String toJson(@NotNull final RequestLogProlog requestLog) {
        return GsonUtils.getGson().toJson(requestLog);
    }
}
