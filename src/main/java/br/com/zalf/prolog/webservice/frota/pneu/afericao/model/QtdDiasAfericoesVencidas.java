package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import org.jetbrains.annotations.NotNull;

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
    private final String qtdDiasAfericaoSulcoVencido;
    private final String qtdDiasAfericaoPressaoVencida;

    public QtdDiasAfericoesVencidas(@NotNull final String nomeUnidade,
                                    @NotNull final String placaVeiculo,
                                    final String qtdDiasAfericaoSulcoVencido,
                                    final String qtdDiasAfericaoPressaoVencida) {
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

    public String getQtdDiasAfericaoSulcoVencido() {
        return qtdDiasAfericaoSulcoVencido;
    }

    public String getQtdDiasAfericaoPressaoVencida() {
        return qtdDiasAfericaoPressaoVencida;
    }
}

