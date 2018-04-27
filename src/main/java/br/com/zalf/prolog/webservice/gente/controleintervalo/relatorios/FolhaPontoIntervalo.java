package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

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
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;

    public FolhaPontoIntervalo(@Nullable final LocalDateTime dataHoraInicio,
                               @Nullable final LocalDateTime dataHoraFim,
                               @NotNull final Long codTipoIntervalo,
                               @NotNull final Long codTipoIntervaloPorUnidade,
                               final boolean trocouDia) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
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