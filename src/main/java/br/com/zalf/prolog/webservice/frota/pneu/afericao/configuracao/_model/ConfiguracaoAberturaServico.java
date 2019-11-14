package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 11/13/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ConfiguracaoAberturaServico {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codUnidadeReferente;
    @NotNull
    private final String nomeUnidadeReferente;
    @Nullable
    private final Long toleranciaCalibragem;
    @Nullable
    private final Long toleranciaInspecao;
    @Nullable
    private final Long sulcoMinimoRecape;
    @Nullable
    private final Long sulcoMinimoDescarte;
    @Nullable
    private final Integer periodoAfericaoPressao;
    @Nullable
    private final Integer periodoAfericaoSulco;

    public ConfiguracaoAberturaServico(@NotNull final Long codigo,
                                       @NotNull final Long codUnidadeReferente,
                                       @NotNull final String nomeUnidadeReferente,
                                       @Nullable final Long toleranciaCalibragem,
                                       @Nullable final Long toleranciaInspecao,
                                       @Nullable final Long sulcoMinimoRecape,
                                       @Nullable final Long sulcoMinimoDescarte,
                                       @Nullable final Integer periodoAfericaoPressao,
                                       @Nullable final Integer periodoAfericaoSulco) {
        this.codigo = codigo;
        this.codUnidadeReferente = codUnidadeReferente;
        this.nomeUnidadeReferente = nomeUnidadeReferente;
        this.toleranciaCalibragem = toleranciaCalibragem;
        this.toleranciaInspecao = toleranciaInspecao;
        this.sulcoMinimoRecape = sulcoMinimoRecape;
        this.sulcoMinimoDescarte = sulcoMinimoDescarte;
        this.periodoAfericaoPressao = periodoAfericaoPressao;
        this.periodoAfericaoSulco = periodoAfericaoSulco;
    }

//    @NotNull
//    public static ConfiguracaoAberturaServico getDummy() {
//        return new ConfiguracaoAberturaServico(
//        );
//    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public Long getCodUnidadeReferente() {
        return codUnidadeReferente;
    }

    @NotNull
    public String getNomeUnidadeReferente() {
        return nomeUnidadeReferente;
    }

    @Nullable
    public Long getToleranciaCalibragem() {
        return toleranciaCalibragem;
    }

    @Nullable
    public Long getToleranciaInspecao() {
        return toleranciaInspecao;
    }

    @Nullable
    public Long getSulcoMinimoRecape() {
        return sulcoMinimoRecape;
    }

    @Nullable
    public Long getSulcoMinimoDescarte() {
        return sulcoMinimoDescarte;
    }

    @Nullable
    public Integer getPeriodoAfericaoPressao() {
        return periodoAfericaoPressao;
    }

    @Nullable
    public Integer getPeriodoAfericaoSulco() {
        return periodoAfericaoSulco;
    }
}