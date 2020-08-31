package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.requester;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-27
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
public final class ErrorResponseAvaCorpAvilan {
    @NotNull
    @SerializedName("Message")
    private final String message;

    @NotNull
    public static ErrorResponseAvaCorpAvilan generateFromString(@NotNull final String jsonErrorBody) {
        return GsonUtils.getGson().fromJson(jsonErrorBody, ErrorResponseAvaCorpAvilan.class);
    }
}
