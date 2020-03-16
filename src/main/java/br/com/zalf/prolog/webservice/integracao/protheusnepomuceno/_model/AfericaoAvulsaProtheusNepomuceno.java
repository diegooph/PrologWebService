package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto que contem as informações do {@link AfericaoAvulsaProtheusNepomuceno pneu} utilizado para a realização da aferição.
 * <p>
 * Todos os atributos do pneu são buscados no endpoint integrado.
 * <p>
 * Created on 10/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class AfericaoAvulsaProtheusNepomuceno {
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
     * Atributo alfanumérico que representa o CPF do colaborador que realizou a aferição do veículo.
     */
    @NotNull
    private final String cpfColaboradorAfericao;

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
     * Constante alfanumérica que representa o {@link TipoMedicaoColetadaAfericao tipo de medição} que foi
     * utilizado para a captura de informações, podem ter sido utilizados 3 tipos:
     * *{@link TipoMedicaoColetadaAfericao#SULCO}
     * *{@link TipoMedicaoColetadaAfericao#PRESSAO}
     * *{@link TipoMedicaoColetadaAfericao#SULCO_PRESSAO}
     */
    @NotNull
    private final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;

    /**
     * Objeto que contém a lista de {@link MedicaoAfericaoProtheusNepomuceno medidas} capturadas em cada pneu aplicado
     * no veículo, podendo ou não incluir os estepes do veículo.
     */
    @NotNull
    private final List<MedicaoAfericaoProtheusNepomuceno> medicoes;

    public AfericaoAvulsaProtheusNepomuceno(@NotNull final String codEmpresa,
                                            @NotNull final String codUnidade,
                                            @NotNull final String cpfColaboradorAfericao,
                                            @NotNull final Long tempoRealizacaoAfericaoInMillis,
                                            @NotNull final LocalDateTime dataHoraAfericaoUtc,
                                            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao,
                                            @NotNull final List<MedicaoAfericaoProtheusNepomuceno> medicoes) {
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.cpfColaboradorAfericao = cpfColaboradorAfericao;
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
        this.dataHoraAfericaoUtc = dataHoraAfericaoUtc;
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
        this.medicoes = medicoes;
    }

    @NotNull
    public static AfericaoAvulsaProtheusNepomuceno getAfericaoDummy() {
        final List<MedicaoAfericaoProtheusNepomuceno> medicoes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            medicoes.add(MedicaoAfericaoProtheusNepomuceno.getMedicaoDummy());
        }
        return new AfericaoAvulsaProtheusNepomuceno(
                "E0001",
                "F0001",
                "000.000.000-00",
                9000L,
                LocalDateTime.now(),
                TipoMedicaoColetadaAfericao.SULCO_PRESSAO,
                medicoes
        );
    }

    @NotNull
    public String getCodEmpresa() { return codEmpresa; }

    @NotNull
    public String getCodUnidade() { return codUnidade; }

    @NotNull
    public String getCpfColaboradorAfericao() { return cpfColaboradorAfericao; }

    @NotNull
    public Long getTempoRealizacaoAfericaoInMillis() { return tempoRealizacaoAfericaoInMillis; }

    @NotNull
    public LocalDateTime getDataHoraAfericaoUtc() { return dataHoraAfericaoUtc; }

    @NotNull
    public TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao() {
        return tipoMedicaoColetadaAfericao;
    }

    @NotNull
    public List<MedicaoAfericaoProtheusNepomuceno> getMedicoes() { return medicoes; }
}
