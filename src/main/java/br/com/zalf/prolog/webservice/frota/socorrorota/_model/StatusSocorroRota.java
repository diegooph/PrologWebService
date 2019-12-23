package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum StatusSocorroRota {
    ABERTO("ABERTO"),
    EM_ATENDIMENTO("EM_ATENDIMENTO"),
    INVALIDO("INVALIDO"),
    FINALIZADO("FINALIZADO");

    @NotNull
    private final String stringRepresentation;

    StatusSocorroRota(@NotNull String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }
    
    public static StatusSocorroRota fromString(String text) throws IllegalArgumentException {
        if (text != null) {
            for (final StatusSocorroRota statusSocorroRota : StatusSocorroRota.values()) {
                if (text.equalsIgnoreCase(statusSocorroRota.stringRepresentation)) {
                    return statusSocorroRota;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum status encontrado para a String: " + text);
    }
}
