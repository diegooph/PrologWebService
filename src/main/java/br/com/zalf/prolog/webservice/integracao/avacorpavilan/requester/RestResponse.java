package br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.ErrorResponseAvaCorpAvilan;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-08-25
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class RestResponse {

    @NotNull
    private final Boolean success;
    @Nullable
    private final ErrorResponseAvaCorpAvilan error;

}
