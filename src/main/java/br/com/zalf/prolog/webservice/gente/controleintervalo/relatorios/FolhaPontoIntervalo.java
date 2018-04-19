package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 19/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FolhaPontoIntervalo {
    @NotNull
    private final LocalDateTime dataHoraInicio;
    @NotNull
    private final LocalDateTime dataHoraFim;
    @NotNull
    private final Long codTipoIntervalo;
    @NotNull
    private final Long codTipoIntervaloPorUnidade;

    public FolhaPontoIntervalo(@NotNull final LocalDateTime dataHoraInicio, @NotNull final LocalDateTime dataHoraFim,
                               @NotNull final Long codTipoIntervalo, @NotNull final Long codTipoIntervaloPorUnidade) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.codTipoIntervalo = codTipoIntervalo;
        this.codTipoIntervaloPorUnidade = codTipoIntervaloPorUnidade;
    }

    @NotNull
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    @NotNull
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