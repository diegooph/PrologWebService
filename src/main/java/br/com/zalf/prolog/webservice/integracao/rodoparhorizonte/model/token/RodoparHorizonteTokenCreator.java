package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token;

import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.RodoparHorizonteConstants;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RodoparHorizonteTokenCreator {

    private RodoparHorizonteTokenCreator() {
        throw new IllegalStateException(
                RodoparHorizonteTokenCreator.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static String createToken(@NotNull final String tokenIntegracao) {
        return RodoparHorizonteConstants.BEARER_TOKEN.concat(tokenIntegracao);
    }
}
