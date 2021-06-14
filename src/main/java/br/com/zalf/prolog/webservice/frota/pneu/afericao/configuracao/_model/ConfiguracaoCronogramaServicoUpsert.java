package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Min;

/**
 * Created on 11/13/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConfiguracaoCronogramaServicoUpsert {
    /**
     * Código do registro de configuração para abertura de serviço
     */
    @Nullable
    private final Long codigo;

    /**
     * Código da empresa
     */
    @NotNull
    private final Long codEmpresaReferente;

    /**
     * Código da regional
     */
    @NotNull
    private final Long codRegionalReferente;

    /**
     * Código da unidade
     */
    @NotNull
    private final Long codUnidadeReferente;

    /**
     * Tolerância da calibragem
     */
    @NotNull
    private final Double toleranciaCalibragem;

    /**
     * Tolerância de inspeção
     */
    @NotNull
    private final Double toleranciaInspecao;

    /**
     * Sulco mínimo para recape
     */
    @NotNull
    private final Double sulcoMinimoRecape;

    /**
     * Sunco mínimo para descarte
     */
    @NotNull
    private final Double sulcoMinimoDescarte;

    /**
     * Período para aferição de pressão
     */
    @NotNull
    @Min(value = 1, message = "Período de aferição pressão menor que 1")
    private final Integer periodoAfericaoPressao;

    /**
     * Período para aferição de sulco
     */
    @NotNull
    @Min(value = 1, message = "Período de aferição sulco menor que 1")
    private final Integer periodoAfericaoSulco;

    public ConfiguracaoCronogramaServicoUpsert(@Nullable final Long codigo,
                                               @NotNull final Long codEmpresaReferente,
                                               @NotNull final Long codRegionalReferente,
                                               @NotNull final Long codUnidadeReferente,
                                               @NotNull final Double toleranciaCalibragem,
                                               @NotNull final Double toleranciaInspecao,
                                               @NotNull final Double sulcoMinimoRecape,
                                               @NotNull final Double sulcoMinimoDescarte,
                                               @NotNull final Integer periodoAfericaoPressao,
                                               @NotNull final Integer periodoAfericaoSulco) {
        this.codigo = codigo;
        this.codEmpresaReferente = codEmpresaReferente;
        this.codRegionalReferente = codRegionalReferente;
        this.codUnidadeReferente = codUnidadeReferente;
        this.toleranciaCalibragem = toleranciaCalibragem;
        this.toleranciaInspecao = toleranciaInspecao;
        this.sulcoMinimoRecape = sulcoMinimoRecape;
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
        this.periodoAfericaoPressao = periodoAfericaoPressao;
        this.periodoAfericaoSulco = periodoAfericaoSulco;
    }

    @NotNull
    public static ConfiguracaoCronogramaServicoUpsert getDummy() {
        return new ConfiguracaoCronogramaServicoUpsert(
                1L,
                3L,
                1L,
                3L,
                0.1D,
                0.2D,
                11.1D,
                11.2D,
                15,
                7
        );
    }

    @Nullable
    public Long getCodigo() { return codigo; }

    @NotNull
    public Long getCodEmpresaReferente() { return codEmpresaReferente; }

    @NotNull
    public Long getCodRegionalReferente() { return codRegionalReferente; }

    @NotNull
    public Long getCodUnidadeReferente() { return codUnidadeReferente; }

    @NotNull
    public Double getToleranciaCalibragem() { return toleranciaCalibragem; }

    @NotNull
    public Double getToleranciaInspecao() { return toleranciaInspecao; }

    @NotNull
    public Double getSulcoMinimoRecape() { return sulcoMinimoRecape; }

    @NotNull
    public Double getSulcoMinimoDescarte() { return sulcoMinimoDescarte; }

    @NotNull
    public Integer getPeriodoAfericaoPressao() { return periodoAfericaoPressao; }

    @NotNull
    public Integer getPeriodoAfericaoSulco() { return periodoAfericaoSulco; }
}