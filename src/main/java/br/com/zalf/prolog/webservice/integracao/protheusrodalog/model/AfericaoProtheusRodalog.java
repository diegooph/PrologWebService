package br.com.zalf.prolog.webservice.integracao.protheusrodalog.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Este objeto representa a {@link AfericaoProtheusRodalog aferição} realizada em um veículo. Neste objeto estarão todas
 * as informações capturadas através do processo de medição do ProLog.
 * <p>
 * Utilizamos um objeto específico para não criar dependência entre a integração com o Protheus, da empresa Rodalog, com
 * as demais integrações, assim, este objeto fica de uso exclusivo para esta integração.
 * <p>
 * Estas informações serão enviadas para um endpoint integrado e este deve estar preparado para receber estas
 * informações neste padrão.
 * <p>
 * Created on 27/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * <p>
 * {@see protheusrodalog}
 */
public final class AfericaoProtheusRodalog {
    /**
     * Atributo alfanumérico que representa a placa do veículo que foi aferido.
     */
    private String placaAfericao;

    /**
     * Código da unidade que a aferição foi realizada. Este atributo tem dependência com o veículo, assim, o código
     * da unidade do veículo deve ser o mesmo que o {@code codUnidade}.
     */
    private Long codUnidade;

    /**
     * Atributo alfanumérico que representa o CPF do colaborador que realizou a aferição do veículo.
     */
    private String cpfColaboradorAfericao;

    /**
     * Valor numérico que representa a quilomentragem (KM) do veículo no momento que foi feita a aferição.
     */
    private Long kmMomentoAfericao;

    /**
     * Valor numérico que representa o montante de tempo que o colaborador demorou para aferir todos os pneus do
     * veículo. Este montante de tempo é representado por este valor em milisegundos.
     */
    private Long tempoRealizacaoAfericaoInMillis;

    /**
     * Data e hora que a aferição foi realizada pelo colaborador. Consiste na data e hora em que a requisição chegou ao
     * servidor do ProLog.
     * <p>
     * A data e hora estão no padrão UTC, exemplo: "2019-02-21T15:30:00-03:00"
     */
    private LocalDateTime dataHora;

    /**
     * Constante alfanumérica que representa o {@link TipoMedicaoAfericaoProtheusRodalog tipo de medição} que foi
     * utilizado para a captura de informações, podem ter sido utilizados 3 tipos:
     * *{@link TipoMedicaoAfericaoProtheusRodalog#SULCO}
     * *{@link TipoMedicaoAfericaoProtheusRodalog#PRESSAO}
     * *{@link TipoMedicaoAfericaoProtheusRodalog#SULCO_PRESSAO}
     */
    private TipoMedicaoAfericaoProtheusRodalog tipoMedicaoColetadaAfericao;

    /**
     * Objeto que contém a lista de {@link MedicaoAfericaoProtheusRodalog medidas} capturadas em cada pneu aplicado no
     * veículo, podendo ou não incluir os estepes do veículo.
     */
    private List<MedicaoAfericaoProtheusRodalog> medicoes;

    public AfericaoProtheusRodalog() {
    }

    @NotNull
    public static AfericaoProtheusRodalog getAfericaoDummy() {
        final AfericaoProtheusRodalog afericao = new AfericaoProtheusRodalog();
        afericao.setPlacaAfericao("PRO0001");
        afericao.setCodUnidade(29L);
        afericao.setCpfColaboradorAfericao("000.000.000-00");
        afericao.setKmMomentoAfericao(101010L);
        afericao.setTempoRealizacaoAfericaoInMillis(90000L);
        afericao.setDataHora(LocalDateTime.now());
        afericao.setTipoMedicaoColetadaAfericao(TipoMedicaoAfericaoProtheusRodalog.SULCO_PRESSAO);
        final List<MedicaoAfericaoProtheusRodalog> medicoes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            medicoes.add(MedicaoAfericaoProtheusRodalog.getMedicaoDummy());
        }
        afericao.setMedicoes(medicoes);
        return afericao;
    }

    public String getPlacaAfericao() {
        return placaAfericao;
    }

    public void setPlacaAfericao(final String placaAfericao) {
        this.placaAfericao = placaAfericao;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public String getCpfColaboradorAfericao() {
        return cpfColaboradorAfericao;
    }

    public void setCpfColaboradorAfericao(final String cpfColaboradorAfericao) {
        this.cpfColaboradorAfericao = cpfColaboradorAfericao;
    }

    public Long getKmMomentoAfericao() {
        return kmMomentoAfericao;
    }

    public void setKmMomentoAfericao(final Long kmMomentoAfericao) {
        this.kmMomentoAfericao = kmMomentoAfericao;
    }

    public Long getTempoRealizacaoAfericaoInMillis() {
        return tempoRealizacaoAfericaoInMillis;
    }

    public void setTempoRealizacaoAfericaoInMillis(final Long tempoRealizacaoAfericaoInMillis) {
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(final LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoMedicaoAfericaoProtheusRodalog getTipoMedicaoColetadaAfericao() {
        return tipoMedicaoColetadaAfericao;
    }

    public void setTipoMedicaoColetadaAfericao(final TipoMedicaoAfericaoProtheusRodalog tipoMedicaoColetadaAfericao) {
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
    }

    public List<MedicaoAfericaoProtheusRodalog> getMedicoes() {
        return medicoes;
    }

    public void setMedicoes(final List<MedicaoAfericaoProtheusRodalog> medicoes) {
        this.medicoes = medicoes;
    }
}
