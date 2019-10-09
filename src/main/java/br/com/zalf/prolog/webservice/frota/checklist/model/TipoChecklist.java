package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoChecklist {
    SAIDA('S'),
    RETORNO('R');

    private final char tipo;

    TipoChecklist(final char tipo) {
        this.tipo = tipo;
    }

    public char asChar() {
        return tipo;
    }

    @NotNull
    public String asString() {
        return String.valueOf(tipo);
    }

    @NotNull
    public static TipoChecklist fromChar(final char text) throws IllegalArgumentException {
        for (final TipoChecklist tipo : TipoChecklist.values()) {
            if (text == tipo.tipo) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
    }

    @NotNull
    public static TipoChecklist fromString(final String text) throws IllegalArgumentException {
        if (text == null || text.length() != 1) {
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
        }

        for (final TipoChecklist tipo : TipoChecklist.values()) {
            if (text.charAt(0) == tipo.tipo) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
    }
}