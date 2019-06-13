package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RodoparHorizonteTokenIntegracao {
    @SerializedName("access_token")
    @NotNull
    private final String token;

    @SerializedName("token_type")
    @NotNull
    private final String tokenType;

    public RodoparHorizonteTokenIntegracao(@NotNull final String token,
                                           @NotNull final String tokenType) {
        this.token = token;
        this.tokenType = tokenType;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    @NotNull
    public String getTokenType() {
        return tokenType;
    }
}
