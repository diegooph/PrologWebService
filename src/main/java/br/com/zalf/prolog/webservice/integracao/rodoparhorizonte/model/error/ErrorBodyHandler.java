package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.error;

import br.com.zalf.prolog.webservice.commons.util.Log;
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
    private static final String TAG = ErrorBodyHandler.class.getSimpleName();
    private static final String DEFAULT_MESSAGE_FOR_INTERNAL_SERVER_ERROR =
            "Erro no Sistema Rodopar, contate os administradores da Horizonte";

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
        try {
            return ProLogError.generateFromString(stringPrologError);
        } catch (final Throwable t) {
            final String msg = String.format("Erro ao realizar o parse da mensagem de erro recebida do Rodopar:\n" +
                    "jsonBody: %s\n" +
                    "stringPrologError: %s", jsonBody, stringPrologError);
            Log.e(TAG, msg, t);
            throw t;
        }
    }

    /**
     * Método utilizado para ler o conteúdo presente no corpo do erro retornado pelo Sistema Rodopar.
     *
     * @param errorBody Body presente em uma error response de uma requisição.
     * @return Um objeto específico para tratar erros de autenticação na integração com o Rodopar.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    public static RodoparHorizonteTokenError getTokenExceptionFromBody(
            @NotNull final ResponseBody errorBody) throws Throwable {
        final String jsonBody = errorBody.string();
        return RodoparHorizonteTokenError.generateFromString(jsonBody);
    }

    /**
     * Método utilizado para tratar corretamente as situações de erro. Este método retorna uma mensagem específica para
     * situações onde ocorram ERRO 500 (Internal Server Error).
     *
     * @param httpStatusCode Código que representa o estado do retorno.
     * @param proLogError    Objeto retornado do Sistema Rodopar contendo as informações sobre o erro ocorrido.
     * @return Uma String que representa a mensagem a ser exibida ao usuário.
     */
    @NotNull
    public static String getErrorMessage(final int httpStatusCode, @NotNull final ProLogError proLogError) {
        // Se for um ERRO 500 ou >500 então retornamos a mensagem padrão.
        if (httpStatusCode >= javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            return DEFAULT_MESSAGE_FOR_INTERNAL_SERVER_ERROR;
        }
        return proLogError.getMessage();
    }
}
