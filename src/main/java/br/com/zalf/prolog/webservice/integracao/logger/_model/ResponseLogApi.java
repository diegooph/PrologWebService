package br.com.zalf.prolog.webservice.integracao.logger._model;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 18/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ResponseLogApi implements RequestResponseLog {
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

    @NotNull
    @Override
    public String toJson() {
        return GsonUtils.getGson().toJson(this);
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }
}
