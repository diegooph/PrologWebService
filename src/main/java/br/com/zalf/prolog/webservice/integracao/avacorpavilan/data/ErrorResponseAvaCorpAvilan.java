package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class ErrorResponseAvaCorpAvilan {

    @NotNull
    final String errorMessage;

    @NotNull
    public static ErrorResponseAvaCorpAvilan generateFromString(
            @NotNull final String jsonErrorBody) {
        return GsonUtils.getGson().fromJson(jsonErrorBody, ErrorResponseAvaCorpAvilan.class);
    }

}
