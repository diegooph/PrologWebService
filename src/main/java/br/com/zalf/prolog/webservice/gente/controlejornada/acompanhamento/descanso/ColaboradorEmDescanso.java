package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @Nullable
    @SerializedName("duracaoUltimaViagemEmSegundos")
    private final Duration duracaoUltimaViagem;
    @NotNull
    @SerializedName("tempoTotalDescansoEmSegundos")
    private final Duration tempoTotalDescanso;
    private final boolean inicioFoiAjustado;
    private final boolean fimFoiAjustado;

    public ColaboradorEmDescanso(@NotNull final String nomeColaborador,
                                 @Nullable final LocalDateTime dataHoraInicioUltimaViagem,
                                 @NotNull final LocalDateTime dataHoraFimUltimaViagem,
                                 @Nullable final Duration duracaoUltimaViagem,
                                 @NotNull final Duration tempoTotalDescanso,
                                 final boolean inicioFoiAjustado,
                                 final boolean fimFoiAjustado) {
        this.nomeColaborador = nomeColaborador;
        this.dataHoraInicioUltimaViagem = dataHoraInicioUltimaViagem;
        this.dataHoraFimUltimaViagem = dataHoraFimUltimaViagem;
        this.duracaoUltimaViagem = duracaoUltimaViagem;
        this.tempoTotalDescanso = tempoTotalDescanso;
        this.inicioFoiAjustado = inicioFoiAjustado;
        this.fimFoiAjustado = fimFoiAjustado;
    }

    @NotNull
    public static ColaboradorEmDescanso createDummy(final boolean fimAvulso) {
        if (fimAvulso) {
            return new ColaboradorEmDescanso(
                    "Carlos Eduardo da Silva",
                    null,
                    // Parou faz 11 horas e 30 minutos.
                    LocalDateTime
                            .now()
                            .minus((11 * 60) + 30, ChronoUnit.MINUTES),
                    Duration.ofMinutes(11 + 30),
                    Duration.ofMinutes((11 * 60) + 30),
                    true,
                    true);
        } else {
            return new ColaboradorEmDescanso(
                    "Emanuel Sebastião Cunha",
                    LocalDateTime
                            .now()
                            .minus(8, ChronoUnit.HOURS),
                    // Parou faz 11 horas e 30 minutos.
                    LocalDateTime
                            .now()
                            .minus((11 * 60) + 30, ChronoUnit.MINUTES),
                    Duration.ofMinutes(11 + 30),
                    Duration.ofMinutes((11 * 60) + 30),
                    false,
                    false);
        }
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

    @Nullable
    public Duration getDuracaoUltimaViagem() {
        return duracaoUltimaViagem;
    }

    @NotNull
    public Duration getTempoTotalDescanso() {
        return tempoTotalDescanso;
    }

    public boolean isInicioFoiAjustado() {
        return inicioFoiAjustado;
    }

    public boolean isFimFoiAjustado() {
        return fimFoiAjustado;
    }
}