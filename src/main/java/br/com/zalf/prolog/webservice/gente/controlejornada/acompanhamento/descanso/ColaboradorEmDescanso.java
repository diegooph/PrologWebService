package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorEmDescanso {
    @NotNull
    private final String nomeColaborador;
    @Nullable
    private final LocalDateTime dataHoraInicioUltimaViagem;
    @NotNull
    private final LocalDateTime dataHoraFimUltimaViagem;
    @NotNull
    @SerializedName("tempoTotalDescansoEmSegundos")
    private final Duration tempoTotalDescanso;

    public ColaboradorEmDescanso(@NotNull final String nomeColaborador,
                                 @Nullable final LocalDateTime dataHoraInicioUltimaViagem,
                                 @NotNull final LocalDateTime dataHoraFimUltimaViagem,
                                 @NotNull final Duration tempoTotalDescanso) {
        this.nomeColaborador = nomeColaborador;
        this.dataHoraInicioUltimaViagem = dataHoraInicioUltimaViagem;
        this.dataHoraFimUltimaViagem = dataHoraFimUltimaViagem;
        this.tempoTotalDescanso = tempoTotalDescanso;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @Nullable
    public LocalDateTime getDataHoraInicioUltimaViagem() {
        return dataHoraInicioUltimaViagem;
    }

    @NotNull
    public LocalDateTime getDataHoraFimUltimaViagem() {
        return dataHoraFimUltimaViagem;
    }

    @NotNull
    public Duration getTempoTotalDescanso() {
        return tempoTotalDescanso;
    }
}