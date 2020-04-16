package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/13/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class InfosTipoVeiculoConfiguracaoAfericao {
    /**
     * Código da Unidade a qual os dados de configuração de aferição se referem.
     */
    @NotNull
    private final Long codUnidade;
    /**
     * Código do Tipo do Veículo a qual os dados de configuração de aferição se referem.
     */
    @NotNull
    private final Long codTipoVeiculo;
    /**
     * Indica se o {@link #codTipoVeiculo} permite aferição do tipo Sulco.
     */
    private final boolean podeAferirSulco;
    /**
     * Indica se o {@link #codTipoVeiculo} permite aferição do tipo Pressao.
     */
    private final boolean podeAferirPressao;
    /**
     * Indica se o {@link #codTipoVeiculo} permite aferição do tipo Sulco e Pressão.
     */
    private final boolean podeAferirSulcoPressao;
    /**
     * Indica se o {@link #codTipoVeiculo} permite aferir estepes, caso o veículo possuir.
     */
    private final boolean podeAferirEstepes;

    public InfosTipoVeiculoConfiguracaoAfericao(@NotNull final Long codUnidade,
                                                @NotNull final Long codTipoVeiculo,
                                                final boolean podeAferirSulco,
                                                final boolean podeAferirPressao,
                                                final boolean podeAferirSulcoPressao,
                                                final boolean podeAferirEstepes) {
        this.codUnidade = codUnidade;
        this.codTipoVeiculo = codTipoVeiculo;
        this.podeAferirSulco = podeAferirSulco;
        this.podeAferirPressao = podeAferirPressao;
        this.podeAferirSulcoPressao = podeAferirSulcoPressao;
        this.podeAferirEstepes = podeAferirEstepes;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }

    public boolean isPodeAferirSulco() {
        return podeAferirSulco;
    }

    public boolean isPodeAferirPressao() {
        return podeAferirPressao;
    }

    public boolean isPodeAferirSulcoPressao() {
        return podeAferirSulcoPressao;
    }

    public boolean isPodeAferirEstepes() {
        return podeAferirEstepes;
    }
}
