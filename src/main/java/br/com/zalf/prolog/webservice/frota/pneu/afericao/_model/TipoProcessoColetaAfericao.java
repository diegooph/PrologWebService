package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum TipoProcessoColetaAfericao {
    PLACA("PLACA"),
    PNEU_AVULSO("PNEU_AVULSO");

    @NotNull
    private final String stringRepresentation;

    TipoProcessoColetaAfericao(@NotNull final String stringRepresentation) {
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
    public static TipoProcessoColetaAfericao fromString(@NotNull final String tipoProcessoAfericao) {
        return Stream.of(TipoProcessoColetaAfericao.values())
                .filter(e -> e.stringRepresentation.equals(tipoProcessoAfericao))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum tipo de processo encontrado para a string: "
                        + tipoProcessoAfericao));
    }
}