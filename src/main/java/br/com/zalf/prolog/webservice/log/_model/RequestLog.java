package br.com.zalf.prolog.webservice.log._model;

import br.com.zalf.prolog.webservice.commons.KeyCaseInsensitiveMultivaluedMap;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 18/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class RequestLog {
    @Nullable
    private final KeyCaseInsensitiveMultivaluedMap<String, String> headers;
    @Nullable
    private final String url;
    @Nullable
    private final String httpMethod;
    @Nullable
    private final String body;

    public boolean isFromApi() {
        return getTokenIntegracao() != null;
    }

    @Nullable
    public String getTokenIntegracao() {
        if (headers != null) {
            return headers.get(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO).stream().findFirst().orElse(null);
        }

        return null;
    }

    @NotNull
    public String toJson() {
        return GsonUtils.getGson().toJson(this);
    }

    public int getStatusCode() {
        return 0;
    }
}
