package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public enum Icone {
    ALIMENTACAO("ALIMENTACAO"),
    DESCANSO("DESCANSO"),
    ESPERA("ESPERA"),
    ESTADIA("ESTADIA"),
    JORNADA("JORNADA");

    private final String nomeIcone;

    Icone(String nomeIcone) {
        this.nomeIcone = nomeIcone;
    }

    public static Icone fromString(@NotNull final String nomeIcone) {
        Preconditions.checkNotNull(nomeIcone);

        for (Icone icone : Icone.values()) {
            if (icone.nomeIcone.equals(nomeIcone)) {
                return icone;
            }
        }

        throw new IllegalArgumentException("Nenhum ícone encontrado com o nome: " + nomeIcone);
    }

    public String getNomeIcone() {
        return nomeIcone;
    }
}