package br.com.zalf.prolog.webservice.permissao.pilares;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-05-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum ImpactoPermissaoProLog {
    BAIXO("BAIXO"),
    MEDIO("MEDIO"),
    ALTO("ALTO"),
    CRITICO("CRITICO");

    @NotNull
    private final String stringRepresentation;

    ImpactoPermissaoProLog(@NotNull String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static ImpactoPermissaoProLog fromString(String text) throws IllegalArgumentException {
        if (text != null) {
            for (final ImpactoPermissaoProLog impacto : ImpactoPermissaoProLog.values()) {
                if (text.equalsIgnoreCase(impacto.stringRepresentation)) {
                    return impacto;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum impacto encontrado para a String: " + text);
    }
}