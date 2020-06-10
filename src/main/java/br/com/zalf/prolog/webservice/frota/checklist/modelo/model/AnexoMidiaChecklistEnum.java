package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-05-13
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AnexoMidiaChecklistEnum {
    BLOQUEADO("BLOQUEADO"),
    OBRIGATORIO("OBRIGATORIO"),
    OPCIONAL("OPCIONAL");

    @NotNull
    private final String stringRepresentation;

    AnexoMidiaChecklistEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static AnexoMidiaChecklistEnum fromString(@Nullable final String s) throws IllegalArgumentException {
        if (s != null) {
            final AnexoMidiaChecklistEnum[] values = AnexoMidiaChecklistEnum.values();
            for (final AnexoMidiaChecklistEnum value : values) {
                if (s.equalsIgnoreCase(value.stringRepresentation)) {
                    return value;
                }
            }
        }

        throw new IllegalArgumentException(String.format("Nenhum enum com valor %s encontrado", s));
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
