package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AfericaoPlacaRodoparHorizonte {
    /**
     * Atributo alfanumérico que representa a placa do veículo que foi aferido.
     */
    @NotNull
    private final String placaAfericao;
    /**
     * Código da unidade em que a aferição foi realizada. Este atributo tem dependência com o veículo, assim, o código
     * da unidade do veículo deve ser o mesmo que o {@code codUnidadeAfericao}.
     */
    @NotNull
    private final Long codUnidadeAfericao;
    /**
     * Atributo alfanumérico que representa o CPF do colaborador que realizou a aferição do veículo.
     */
    @NotNull
    private final String cpfColaboradorAfericao;
    /**
     * Valor numérico que representa a quilometragem (KM) do veículo no momento que foi feita a aferição.
     */
    @NotNull
    private final Long kmMomentoAfericao;
    /**
     * Valor numérico que representa o montante de tempo que o colaborador demorou para aferir todos os pneus do
     * veículo. Este montante de tempo é representado por este valor em milissegundos.
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
     * utilizado para a captura de informações, podem ter sido utilizados 3 tipos:
     * *{@link TipoMedicaoAfericaoRodoparHorizonte#SULCO}
     * *{@link TipoMedicaoAfericaoRodoparHorizonte#PRESSAO}
     * *{@link TipoMedicaoAfericaoRodoparHorizonte#SULCO_PRESSAO}
     */
    @NotNull
    private final TipoMedicaoAfericaoRodoparHorizonte tipoMedicaoColetadaAfericao;
    /**
     * Objeto que contém a lista de {@link MedicaoAfericaoRodoparHorizonte medidas} capturadas em cada pneu aplicado no
     * veículo, podendo ou não incluir os estepes do veículo.
     */
    @NotNull
    private final List<MedicaoAfericaoRodoparHorizonte> medicoes;

    public AfericaoPlacaRodoparHorizonte(@NotNull final String placaAfericao,
                                         @NotNull final Long codUnidadeAfericao,
                                         @NotNull final String cpfColaboradorAfericao,
                                         @NotNull final Long kmMomentoAfericao,
                                         @NotNull final Long tempoRealizacaoAfericaoInMillis,
                                         @NotNull final LocalDateTime dataHoraAfericaoUtc,
                                         @NotNull final TipoMedicaoAfericaoRodoparHorizonte tipoMedicaoColetadaAfericao,
                                         @NotNull final List<MedicaoAfericaoRodoparHorizonte> medicoes) {
        this.placaAfericao = placaAfericao;
        this.codUnidadeAfericao = codUnidadeAfericao;
        this.cpfColaboradorAfericao = cpfColaboradorAfericao;
        this.kmMomentoAfericao = kmMomentoAfericao;
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
        this.dataHoraAfericaoUtc = dataHoraAfericaoUtc;
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
        this.medicoes = medicoes;
    }

    @NotNull
    public static AfericaoPlacaRodoparHorizonte getDummy() {
        final List<MedicaoAfericaoRodoparHorizonte> medicoes = new ArrayList<>();
        medicoes.add(MedicaoAfericaoRodoparHorizonte.getDummy(TipoMedicaoAfericaoRodoparHorizonte.SULCO_PRESSAO));
        return new AfericaoPlacaRodoparHorizonte(
                "PRO0001",
                5L,
                "03383283194",
                987654L,
                Duration.ofMinutes(10L).toMillis(),
                Now.getLocalDateTimeUtc(),
                TipoMedicaoAfericaoRodoparHorizonte.SULCO_PRESSAO,
                medicoes);
    }

    @NotNull
    public String getPlacaAfericao() {
        return placaAfericao;
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
    public Long getKmMomentoAfericao() {
        return kmMomentoAfericao;
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
    public List<MedicaoAfericaoRodoparHorizonte> getMedicoes() {
        return medicoes;
    }
}
