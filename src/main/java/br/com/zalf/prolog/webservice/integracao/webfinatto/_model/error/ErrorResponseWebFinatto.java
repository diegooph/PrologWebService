package br.com.zalf.prolog.webservice.integracao.webfinatto._model.error;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ErrorResponseWebFinatto {
    @NotNull
    @SerializedName("Error")
    final String errorMessage;

    @NotNull
    public static ErrorResponseWebFinatto generateFromString(@NotNull final String jsonErrorBody) {
        return GsonUtils.getGson().fromJson(jsonErrorBody, ErrorResponseWebFinatto.class);
    }
}