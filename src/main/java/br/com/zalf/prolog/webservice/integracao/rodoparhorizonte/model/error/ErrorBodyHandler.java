package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error;

import br.com.zalf.prolog.webservice.errorhandling.error.ProLogError;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenError;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ErrorBodyHandler {
    /**
     * Este método receberá o JSON presente na mensagem de erro da integração entre ProLog e Rodopar e retornará um
     * {@link ProLogError}.
     * <p>
     * Por limitações do Rodopar, não é possível retornar uma estrutura complexa como Response, então, para os casos de
     * erro o Rodopar está retornando um atributo <code>Message</code> e dentro dele está uma string que representa um
     * {@link ProLogError}.
     *
     * @param errorBody Body presente em uma error response de uma requisição.
     * @return Um objeto {@link ProLogError} contendo as informações informados no {@code jsonBody}.
     */
    @NotNull
    public static ProLogError getProLogErrorFromBody(@NotNull final ResponseBody errorBody) throws Throwable {
        final String jsonBody = errorBody.string();
        // O JSON está encapsulado em uma tag Message = {"Message":"{...}"}, precisamos apenas da parte interna.
        final String stringError = jsonBody.substring("{\"Message\":\"".length(), jsonBody.length() - "\"}".length());
        // Removemos os caracteres especiais para que o parse do JSON fique correto.
        final String stringPrologError = stringError.replaceAll("\\\\", "");
        return ProLogError.generateFromString(stringPrologError);
    }

    @NotNull
    public static RodoparHorizonteTokenError getTokenExceptionFromBody(
            @NotNull final ResponseBody errorBody) throws Throwable {
        final String jsonBody = errorBody.string();
        return RodoparHorizonteTokenError.generateFromString(jsonBody);
    }
}
