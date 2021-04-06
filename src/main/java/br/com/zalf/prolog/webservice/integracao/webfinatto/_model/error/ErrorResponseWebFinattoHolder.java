package br.com.zalf.prolog.webservice.integracao.webfinatto._model.error;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class ErrorResponseWebFinattoHolder {
    @NotNull
    final List<ErrorResponseWebFinatto> errorMessage;

    @NotNull
    public static List<ErrorResponseWebFinatto> generateFromString(@NotNull final String jsonErrorBody) {
        return GsonUtils.getGson().fromJson(jsonErrorBody,
                                            new TypeToken<List<ErrorResponseWebFinatto>>() {}.getType());
    }
}