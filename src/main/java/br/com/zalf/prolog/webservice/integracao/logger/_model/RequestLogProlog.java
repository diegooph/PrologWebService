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
public final class RequestLogProlog implements RequestResponseLog {
    @NotNull
    private final String method;
    @NotNull
    private final String url;
    @Nullable
    private final Map<String, List<String>> headers;
    @Nullable
    private final String contentType;
    @Nullable
    private final String body;

    @NotNull
    @Override
    public String toJson() {
        return GsonUtils.getGson().toJson(this);
    }

    @Override
    public int getStatusCode() {
        return 0;
    }
}
