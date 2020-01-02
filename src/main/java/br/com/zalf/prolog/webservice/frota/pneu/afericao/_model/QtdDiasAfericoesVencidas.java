package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 22/11/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class QtdDiasAfericoesVencidas {
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final String placaVeiculo;
    private final boolean podeAferirSulco;
    private final boolean podeAferirPressao;
    @NotNull
    private final Optional<Integer> qtdDiasAfericaoSulcoVencido;
    @NotNull
    private final Optional<Integer> qtdDiasAfericaoPressaoVencida;

    public QtdDiasAfericoesVencidas(@NotNull final String nomeUnidade,
                                    @NotNull final String placaVeiculo,
                                    final boolean podeAferirSulco,
                                    final boolean podeAferirPressao,
                                    @NotNull final Optional<Integer> qtdDiasAfericaoSulcoVencido,
                                    @NotNull final Optional<Integer> qtdDiasAfericaoPressaoVencida) {
        this.nomeUnidade = nomeUnidade;
        this.placaVeiculo = placaVeiculo;
        this.podeAferirSulco = podeAferirSulco;
        this.podeAferirPressao = podeAferirPressao;
        this.qtdDiasAfericaoSulcoVencido = qtdDiasAfericaoSulcoVencido;
        this.qtdDiasAfericaoPressaoVencida = qtdDiasAfericaoPressaoVencida;
    }

    @NotNull
    public String getNomeUnidade() {
        return nomeUnidade;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public boolean isPodeAferirSulco() {
        return podeAferirSulco;
    }

    public boolean isPodeAferirPressao() {
        return podeAferirPressao;
    }

    @NotNull
    public Optional<Integer> getQtdDiasAfericaoSulcoVencido() {
        return qtdDiasAfericaoSulcoVencido;
    }

    @NotNull
    public Optional<Integer> getQtdDiasAfericaoPressaoVencida() {
        return qtdDiasAfericaoPressaoVencida;
    }
}

