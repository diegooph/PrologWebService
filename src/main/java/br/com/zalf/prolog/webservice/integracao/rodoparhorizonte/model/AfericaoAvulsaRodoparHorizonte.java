package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created on 04/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AfericaoAvulsaRodoparHorizonte {
    /**
     * Código da unidade em que a aferição foi realizada. Este atributo tem dependência com o pneu, assim, o código
     * da unidade do pneu deve ser o mesmo que o {@code codUnidadeAfericao}.
     */
    @NotNull
    private final Long codUnidadeAfericao;
    /**
     * Atributo alfanumérico que representa o CPF do colaborador que realizou a aferição do pneu.
     */
    @NotNull
    private final String cpfColaboradorAfericao;
    /**
     * Valor numérico que representa o montante de tempo que o colaborador demorou para aferir o pneu.
     * Este montante de tempo é representado por um valor em milissegundos.
     */
    @NotNull
    private final Long tempoRealizacaoAfericaoInMillis;
    /**
     * Data e hora em que a aferição foi realizada pelo colaborador. Consiste na data e hora em que a requisição chegou
     * ao servidor do ProLog.
     * <p>
     * A data e hora estão em UTC, ou seja, já com o deslocamento aplicado, exemplo: "2019-02-21T15:30:00"
     */
    @NotNull
    private final LocalDateTime dataHoraAfericaoUtc;
    /**
     * Constante alfanumérica que representa o {@link TipoMedicaoAfericaoRodoparHorizonte tipo de medição} que foi
     * utilizado para a captura de informações, no processo de medição de um pneu avulso pode ser utilizado apenas:
     * *{@link TipoMedicaoAfericaoRodoparHorizonte#SULCO}
     */
    @NotNull
    private final TipoMedicaoAfericaoRodoparHorizonte tipoMedicaoColetadaAfericao;
    /**
     * Objeto que contém as {@link MedicaoAfericaoRodoparHorizonte medidas} capturadas no pneu.
     */
    @NotNull
    private final MedicaoAfericaoRodoparHorizonte medicao;

    public AfericaoAvulsaRodoparHorizonte(
            @NotNull final Long codUnidadeAfericao,
            @NotNull final String cpfColaboradorAfericao,
            @NotNull final Long tempoRealizacaoAfericaoInMillis,
            @NotNull final LocalDateTime dataHoraAfericaoUtc,
            @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoMedicaoColetadaAfericao,
            @NotNull final MedicaoAfericaoRodoparHorizonte medicao) {
        this.codUnidadeAfericao = codUnidadeAfericao;
        this.cpfColaboradorAfericao = cpfColaboradorAfericao;
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
        this.dataHoraAfericaoUtc = dataHoraAfericaoUtc;
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
        this.medicao = medicao;
    }

    @NotNull
    public static AfericaoAvulsaRodoparHorizonte getDummy() {
        return new AfericaoAvulsaRodoparHorizonte(
                5L,
                "03383283194",
                Duration.ofMinutes(1L).toMillis(),
                Now.getLocalDateTimeUtc(),
                TipoMedicaoAfericaoRodoparHorizonte.SULCO,
                MedicaoAfericaoRodoparHorizonte.getDummy(TipoMedicaoAfericaoRodoparHorizonte.SULCO));
    }

    @NotNull
    public Long getCodUnidadeAfericao() {
        return codUnidadeAfericao;
    }

    @NotNull
    public String getCpfColaboradorAfericao() {
        return cpfColaboradorAfericao;
    }

    @NotNull
    public Long getTempoRealizacaoAfericaoInMillis() {
        return tempoRealizacaoAfericaoInMillis;
    }

    @NotNull
    public LocalDateTime getDataHoraAfericaoUtc() {
        return dataHoraAfericaoUtc;
    }

    @NotNull
    public TipoMedicaoAfericaoRodoparHorizonte getTipoMedicaoColetadaAfericao() {
        return tipoMedicaoColetadaAfericao;
    }

    @NotNull
    public MedicaoAfericaoRodoparHorizonte getMedicao() {
        return medicao;
    }
}
