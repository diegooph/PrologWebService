package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 11/13/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConfiguracaoAberturaServicoUpsert {
    /**
    * Código do registro de configuração para abertura de serviço
    * */
    @NotNull
    private final Long codigo;
    /**
     * Código da empresa
     * */
    @NotNull
    private final Long codEmpresaReferente;
    /**
     * Código da regional
     * */
    @NotNull
    private final Long codRegionalReferente;
    /**
     * Código da unidade
     * */
    @NotNull
    private final Long codUnidadeReferente;
    /**
     * Tolerância da calibragem
     * */
    @Nullable
    private final Double toleranciaCalibragem;
    /**
     * Tolerância de inspeção
     * */
    @Nullable
    private final Double toleranciaInspecao;
    /**
     * Sulco mínimo para recape
     * */
    @Nullable
    private final Double sulcoMinimoRecape;
    /**
     * Sunco mínimo para descarte
     * */
    @Nullable
    private final Double sulcoMinimoDescarte;
    /**
     * Período para aferição de pressão
     * */
    @Nullable
    private final Integer periodoAfericaoPressao;
    /**
     * Período para aferição de sulco
     * */
    @Nullable
    private final Integer periodoAfericaoSulco;

    public ConfiguracaoAberturaServicoUpsert(@Nullable final Long codigo,
                                             @NotNull final Long codEmpresaReferente,
                                             @NotNull final Long codRegionalReferente,
                                             @NotNull final Long codUnidadeReferente,
                                             @Nullable final Double toleranciaCalibragem,
                                             @Nullable final Double toleranciaInspecao,
                                             @Nullable final Double sulcoMinimoRecape,
                                             @Nullable final Double sulcoMinimoDescarte,
                                             @Nullable final Integer periodoAfericaoPressao,
                                             @Nullable final Integer periodoAfericaoSulco) {
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
    public static ConfiguracaoAberturaServicoUpsert getDummy() {
        return new ConfiguracaoAberturaServicoUpsert(
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

    @Nullable
    public Double getToleranciaCalibragem() { return toleranciaCalibragem; }

    @Nullable
    public Double getToleranciaInspecao() { return toleranciaInspecao; }

    @Nullable
    public Double getSulcoMinimoRecape() { return sulcoMinimoRecape; }

    @Nullable
    public Double getSulcoMinimoDescarte() { return sulcoMinimoDescarte; }

    @Nullable
    public Integer getPeriodoAfericaoPressao() { return periodoAfericaoPressao; }

    @Nullable
    public Integer getPeriodoAfericaoSulco() { return periodoAfericaoSulco; }
}