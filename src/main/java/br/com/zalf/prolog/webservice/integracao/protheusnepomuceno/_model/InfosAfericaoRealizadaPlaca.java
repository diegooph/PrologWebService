package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/13/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class InfosAfericaoRealizadaPlaca {
    /**
     * Placa do Veículo que as informações se referem.
     */
    @NotNull
    private final String placaVeiculo;
    /**
     * Número inteiro que representa a quantidade de dias desde a última aferição de Sulco. Este número será -1 caso
     * nunca tenha sido aferido.
     */
    private final int diasUltimaAfericaoSulco;
    /**
     * Número inteiro que representa a quantidade de dias desde a última aferição da Pressão. Este número será -1 caso
     * nunca tenha sido aferido.
     */
    private final int diasUltimaAfericaoPressao;

    public InfosAfericaoRealizadaPlaca(@NotNull final String placaVeiculo,
                                       final int diasUltimaAfericaoSulco,
                                       final int diasUltimaAfericaoPressao) {
        this.placaVeiculo = placaVeiculo;
        this.diasUltimaAfericaoSulco = diasUltimaAfericaoSulco;
        this.diasUltimaAfericaoPressao = diasUltimaAfericaoPressao;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public int getDiasUltimaAfericaoSulco() {
        return diasUltimaAfericaoSulco;
    }

    public int getDiasUltimaAfericaoPressao() {
        return diasUltimaAfericaoPressao;
    }
}
