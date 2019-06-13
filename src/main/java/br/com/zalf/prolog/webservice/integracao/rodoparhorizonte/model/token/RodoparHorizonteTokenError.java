package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RodoparHorizonteTokenError {
    @SerializedName("error")
    @NotNull
    private final String error;

    @SerializedName("error_description")
    @NotNull
    private final String errorDescription;

    public RodoparHorizonteTokenError(@NotNull final String error, @NotNull final String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    @NotNull
    public static RodoparHorizonteTokenError generateFromString(@NotNull final String jsonBody) {
        return GsonUtils.getGson().fromJson(jsonBody, RodoparHorizonteTokenError.class);
    }

    @NotNull
    public String getError() {
        return error;
    }

    @NotNull
    public String getErrorDescription() {
        return errorDescription;
    }
}
