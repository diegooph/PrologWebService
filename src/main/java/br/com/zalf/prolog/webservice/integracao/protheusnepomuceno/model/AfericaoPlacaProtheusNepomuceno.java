package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.model;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Este objeto representa a {@link AfericaoPlacaProtheusNepomuceno aferição} realizada em um veículo.
 * Neste objeto estarão todas as informações capturadas através do processo de medição do ProLog.
 * <p>
 * Utilizamos um objeto específico para não criar dependência entre a integração com o Protheus, da empresa Nepomuceno,
 * com as demais integrações, assim, este objeto fica de uso exclusivo para esta integração.
 * <p>
 * Estas informações serão enviadas para um endpoint integrado e este deve estar preparado para receber estas
 * informações neste padrão.
 * <p>
 * Created on 10/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 * <p>
 * {@see protheusnepomuceno}
 */
public final class AfericaoPlacaProtheusNepomuceno {
    /**
     * Atributo alfanumérico que representa o código da empresa do cliente
     */
    @NotNull
    private final String codEmpresa;

    /**
     * Atributo alfanumérico que representa o código da unidade do cliente
     */
    @NotNull
    private final String codUnidade;

    /**
     * Atributo alfanumérico que representa a placa do veículo que foi aferido.
     */
    @NotNull
    private final String placaAfericao;

    /**
     * Atributo alfanumérico que representa o CPF do colaborador que realizou a aferição do veículo.
     */
    @NotNull
    private final String cpfColaboradorAfericao;

    /**
     * Valor numérico que representa a quilomentragem (KM) do veículo no momento que foi feita a aferição.
     */
    @NotNull
    private final Long kmMomentoAfericao;

    /**
     * Valor numérico que representa o montante de tempo que o colaborador demorou para aferir todos os pneus do
     * veículo. Este montante de tempo é representado por este valor em milisegundos.
     */
    @NotNull
    private final Long tempoRealizacaoAfericaoInMillis;

    /**
     * Data e hora que a aferição foi realizada pelo colaborador. Consiste na data e hora em que a requisição chegou ao
     * servidor do ProLog.
     * <p>
     * A data e hora estão no padrão UTC, exemplo: "2019-02-21T15:30:00-03:00"
     */
    @NotNull
    private final LocalDateTime dataHoraAfericaoUtc;

    /**
     * Constante alfanumérica que representa o {@link TipoMedicaoAfericaoProtheusNepomuceno tipo de medição} que foi
     * utilizado para a captura de informações, podem ter sido utilizados 3 tipos:
     * *{@link TipoMedicaoAfericaoProtheusNepomuceno#SULCO}
     * *{@link TipoMedicaoAfericaoProtheusNepomuceno#PRESSAO}
     * *{@link TipoMedicaoAfericaoProtheusNepomuceno#SULCO_PRESSAO}
     */
    @NotNull
    private final TipoMedicaoAfericaoProtheusNepomuceno tipoMedicaoColetadaAfericao;

    /**
     * Objeto que contém a lista de {@link MedicaoAfericaoProtheusNepomuceno medidas} capturadas em cada pneu aplicado
     * no veículo, podendo ou não incluir os estepes do veículo.
     */
    @NotNull
    private final List<MedicaoAfericaoProtheusNepomuceno> medicoes;

    public AfericaoPlacaProtheusNepomuceno(@NotNull final String codEmpresa,
                                           @NotNull final String codUnidade,
                                           @NotNull final String placaAfericao,
                                           @NotNull final String cpfColaboradorAfericao,
                                           @NotNull final Long kmMomentoAfericao,
                                           @NotNull final Long tempoRealizacaoAfericaoInMillis,
                                           @NotNull final LocalDateTime dataHoraAfericaoUtc,
                                           @NotNull final TipoMedicaoAfericaoProtheusNepomuceno tipoMedicaoColetadaAfericao,
                                           @NotNull final List<MedicaoAfericaoProtheusNepomuceno> medicoes) {
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.placaAfericao = placaAfericao;
        this.cpfColaboradorAfericao = cpfColaboradorAfericao;
        this.kmMomentoAfericao = kmMomentoAfericao;
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
        this.dataHoraAfericaoUtc = dataHoraAfericaoUtc;
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
        this.medicoes = medicoes;
    }

    @NotNull
    public static AfericaoPlacaProtheusNepomuceno getAfericaoDummy() {
        final List<MedicaoAfericaoProtheusNepomuceno> medicoes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            medicoes.add(MedicaoAfericaoProtheusNepomuceno.getMedicaoDummy());
        }
        return new AfericaoPlacaProtheusNepomuceno(
                "E0001",
                "F0001",
                "ZZZ0000",
                "000.000.000-00",
                101010L,
                90000L,
                LocalDateTime.now(),
                TipoMedicaoAfericaoProtheusNepomuceno.SULCO_PRESSAO,
                medicoes
        );
    }

    @NotNull
    public String getCodEmpresa() { return codEmpresa; }

    @NotNull
    public String getCodUnidade() { return codUnidade; }

    @NotNull
    public String getPlacaAfericao() { return placaAfericao; }

    @NotNull
    public String getCpfColaboradorAfericao() { return cpfColaboradorAfericao; }

    @NotNull
    public Long getKmMomentoAfericao() { return kmMomentoAfericao; }

    @NotNull
    public Long getTempoRealizacaoAfericaoInMillis() { return tempoRealizacaoAfericaoInMillis; }

    @NotNull
    public LocalDateTime getDataHoraAfericaoUtc() { return dataHoraAfericaoUtc; }

    @NotNull
    public TipoMedicaoAfericaoProtheusNepomuceno getTipoMedicaoColetadaAfericao() {
        return tipoMedicaoColetadaAfericao;
    }

    @NotNull
    public List<MedicaoAfericaoProtheusNepomuceno> getMedicoes() { return medicoes; }
}
