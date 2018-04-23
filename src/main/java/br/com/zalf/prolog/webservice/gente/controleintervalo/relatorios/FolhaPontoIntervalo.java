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

    public FolhaPontoIntervalo(@Nullable final LocalDateTime dataHoraInicio, @Nullable final LocalDateTime dataHoraFim,
                               @NotNull final Long codTipoIntervalo, @NotNull final Long codTipoIntervaloPorUnidade) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.codTipoIntervalo = codTipoIntervalo;
        this.codTipoIntervaloPorUnidade = codTipoIntervaloPorUnidade;
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
}