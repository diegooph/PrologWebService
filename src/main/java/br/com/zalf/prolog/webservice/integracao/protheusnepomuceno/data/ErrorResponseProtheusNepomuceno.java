package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-06-09
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ErrorResponseProtheusNepomuceno {
    final int errorCode;
    @NotNull
    final String errorMessage;

    @NotNull
    public static ErrorResponseProtheusNepomuceno generateFromString(@NotNull final String jsonErrorBody) {
        return GsonUtils.getGson().fromJson(jsonErrorBody, ErrorResponseProtheusNepomuceno.class);
    }
}
