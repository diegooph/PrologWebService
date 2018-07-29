package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 19/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FolhaPontoIntervalo {
    @Nullable
    private final LocalDateTime dataHoraInicio;
    @Nullable
    private final LocalDateTime dataHoraFim;
    @NotNull
    private final Long codTipoIntervalo;
    @NotNull
    private final Long codTipoIntervaloPorUnidade;

    /**
     * Utilizada apenas para cálculos não será serializada no JSON.
     */
    @Exclude
    @Nullable
    private final LocalDateTime dataHoraInicioUtc;
    /**
     * Utilizada apenas para cálculos não será serializada no JSON.
     */
    @Exclude
    @Nullable
    private final LocalDateTime dataHoraFimUtc;

    /**
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;

    public FolhaPontoIntervalo(@Nullable final LocalDateTime dataHoraInicio,
                               @Nullable final LocalDateTime dataHoraFim,
                               @Nullable final LocalDateTime dataHoraInicioUtc,
                               @Nullable final LocalDateTime dataHoraFimUtc,
                               @NotNull final Long codTipoIntervalo,
                               @NotNull final Long codTipoIntervaloPorUnidade,
                               final boolean trocouDia) {
        if ((dataHoraInicio != null && dataHoraInicioUtc == null)
                || ((dataHoraInicioUtc != null && dataHoraInicio == null))) {
            throw new IllegalStateException("A data/hora de início formatada e em UTC devem ser diferentes de null");
        }
        if ((dataHoraFim != null && dataHoraFimUtc == null)
                || ((dataHoraFimUtc != null && dataHoraFim == null))) {
            throw new IllegalStateException("A data/hora de fim formatada e em UTC devem ser diferentes de null");
        }

        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.dataHoraInicioUtc = dataHoraInicioUtc;
        this.dataHoraFimUtc = dataHoraFimUtc;
        this.codTipoIntervalo = codTipoIntervalo;
        this.codTipoIntervaloPorUnidade = codTipoIntervaloPorUnidade;
        this.trocouDia = trocouDia;
    }

    @Nullable
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    @Nullable
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    @Nullable
    public LocalDateTime getDataHoraInicioUtc() {
        return dataHoraInicioUtc;
    }

    @Nullable
    public LocalDateTime getDataHoraFimUtc() {
        return dataHoraFimUtc;
    }

    @NotNull
    public Long getCodTipoIntervalo() {
        return codTipoIntervalo;
    }

    @NotNull
    public Long getCodTipoIntervaloPorUnidade() {
        return codTipoIntervaloPorUnidade;
    }

    public boolean isTrocouDia() {
        return trocouDia;
    }
}