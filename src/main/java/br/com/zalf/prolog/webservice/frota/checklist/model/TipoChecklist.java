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
    public static TipoChecklist fromChar(final char text) throws IllegalArgumentException {
        for (final TipoChecklist tipo : TipoChecklist.values()) {
            if (text == tipo.tipo) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Nenhum enum com esse valor encontrado: " + text);
    }
}