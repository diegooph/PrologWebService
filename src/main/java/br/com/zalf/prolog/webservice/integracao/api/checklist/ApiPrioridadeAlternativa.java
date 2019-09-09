package br.com.zalf.prolog.webservice.integracao.api.checklist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum ApiPrioridadeAlternativa {
    CRITICA("CRITICA"),
    ALTA("ALTA"),
    BAIXA("BAIXA");

    @NotNull
    private final String stringRepresentation;

    ApiPrioridadeAlternativa(@NotNull final String prioridade) {
        this.stringRepresentation = prioridade;
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
    public static ApiPrioridadeAlternativa fromString(
            @Nullable final String prioridade) throws IllegalArgumentException {
        if (prioridade != null) {
            for (final ApiPrioridadeAlternativa prioridadeAlternativa : ApiPrioridadeAlternativa.values()) {
                if (prioridade.equalsIgnoreCase(prioridadeAlternativa.stringRepresentation)) {
                    return prioridadeAlternativa;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma prioridade encontrada para a String: " + prioridade);
    }
}
