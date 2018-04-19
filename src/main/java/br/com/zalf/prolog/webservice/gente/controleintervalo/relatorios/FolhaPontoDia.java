package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

public final class FolhaPontoDia {
    @NotNull
    private final LocalDate diaReferencia;
    @NotNull
    private final List<FolhaPontoIntervalo> intervalosDia;

    public FolhaPontoDia(@NotNull LocalDate diaReferencia, @NotNull List<FolhaPontoIntervalo> intervalosDia) {
        this.diaReferencia = diaReferencia;
        this.intervalosDia = intervalosDia;
    }

    @NotNull
    public LocalDate getDiaReferencia() {
        return diaReferencia;
    }

    @NotNull
    public List<FolhaPontoIntervalo> getIntervalosDia() {
        return intervalosDia;
    }
}