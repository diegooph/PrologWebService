package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public final class FolhaPontoDia {
    @NotNull
    private final LocalDate diaReferencia;
    @NotNull
    private final List<Intervalo> intervalosDia;

    public FolhaPontoDia(@NotNull LocalDate diaReferencia, @NotNull List<Intervalo> intervalosDia) {
        this.diaReferencia = diaReferencia;
        this.intervalosDia = intervalosDia;
    }

    @NotNull
    public LocalDate getDiaReferencia() {
        return diaReferencia;
    }

    @NotNull
    public List<Intervalo> getIntervalosDia() {
        return intervalosDia;
    }
}