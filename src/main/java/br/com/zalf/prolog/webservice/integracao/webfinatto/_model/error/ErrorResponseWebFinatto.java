package br.com.zalf.prolog.webservice.integracao.webfinatto._model.error;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ErrorResponseWebFinatto {
    @NotNull
    @SerializedName("Error")
    private final String errorMessage;
}
