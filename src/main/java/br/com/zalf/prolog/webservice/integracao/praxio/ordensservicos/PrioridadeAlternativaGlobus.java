package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 23/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum PrioridadeAlternativaGlobus {
    CRITICA("CRITICA"),
    ALTA("ALTA"),
    BAIXA("BAIXA");

    @NotNull
    private final String stringRepresentation;

    PrioridadeAlternativaGlobus(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @NotNull
    public static PrioridadeAlternativaGlobus fromString(@NotNull final String prioridade) {
        for (final PrioridadeAlternativaGlobus prioridadeAlternativa : PrioridadeAlternativaGlobus.values()) {
            if (prioridade.equals(prioridadeAlternativa.stringRepresentation)) {
                return prioridadeAlternativa;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de prioridade encontrado para a string: " + prioridade);
    }

    @NotNull
    @Override
    public String toString() {
        return asString();
    }
}
