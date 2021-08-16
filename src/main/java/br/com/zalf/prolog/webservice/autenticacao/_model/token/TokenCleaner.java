package br.com.zalf.prolog.webservice.autenticacao._model.token;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TokenCleaner {

    private TokenCleaner() {
        throw new IllegalStateException(TokenCleaner.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String getOnlyToken(@NotNull final String fullToken) {
        Preconditions.checkNotNull(fullToken, "fullToken não pode ser nulo!");

        if (fullToken.contains("Bearer")) {
            return fullToken.substring("Bearer".length()).trim();
        } else if (fullToken.contains("Basic")) {
            return fullToken.substring("Basic".length()).trim();
        }
        return fullToken;
    }
}