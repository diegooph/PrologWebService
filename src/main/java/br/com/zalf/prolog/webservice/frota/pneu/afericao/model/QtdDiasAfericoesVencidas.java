package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 22/11/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class QtdDiasAfericoesVencidas {
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Optional<Integer> qtdDiasAfericaoSulcoVencido;
    @NotNull
    private final Optional<Integer> qtdDiasAfericaoPressaoVencida;

    public QtdDiasAfericoesVencidas(@NotNull final String nomeUnidade,
                                    @NotNull final String placaVeiculo,
                                    @NotNull final Optional<Integer> qtdDiasAfericaoSulcoVencido,
                                    @NotNull final Optional<Integer> qtdDiasAfericaoPressaoVencida) {
        this.nomeUnidade = nomeUnidade;
        this.placaVeiculo = placaVeiculo;
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

    @NotNull
    public Optional<Integer> getQtdDiasAfericaoSulcoVencido() {
        return qtdDiasAfericaoSulcoVencido;
    }

    @NotNull
    public Optional<Integer> getQtdDiasAfericaoPressaoVencida() {
        return qtdDiasAfericaoPressaoVencida;
    }
}

