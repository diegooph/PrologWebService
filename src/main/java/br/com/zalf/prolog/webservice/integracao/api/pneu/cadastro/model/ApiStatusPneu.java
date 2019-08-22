package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 29/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum ApiStatusPneu {
    EM_USO("EM_USO"),
    ESTOQUE("ESTOQUE"),
    ANALISE("ANALISE"),
    DESCARTE("DESCARTE");

    @NotNull
    private final String stringRepresentation;

    ApiStatusPneu(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @NotNull
    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static ApiStatusPneu fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final ApiStatusPneu statusPneu : ApiStatusPneu.values()) {
                if (text.equalsIgnoreCase(statusPneu.stringRepresentation)) {
                    return statusPneu;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum status encontrado para a String: " + text);
    }
}
