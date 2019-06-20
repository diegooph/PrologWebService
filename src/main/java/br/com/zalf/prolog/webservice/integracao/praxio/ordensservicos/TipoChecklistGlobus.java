package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 23/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoChecklistGlobus {
    SAIDA("SAIDA"),
    RETORNO("RETORNO");

    @NotNull
    private final String stringRepresentation;

    TipoChecklistGlobus(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @NotNull
    public static TipoChecklistGlobus fromString(@NotNull final String tipo) {
        for (final TipoChecklistGlobus tipoChecklist : TipoChecklistGlobus.values()) {
            if (tipo.equals(tipoChecklist.stringRepresentation)) {
                return tipoChecklist;
            }
        }
        throw new IllegalArgumentException("Nenhum tipo de checklist encontrado para a string: " + tipo);
    }

    @NotNull
    @Override
    public String toString() {
        return asString();
    }
}
