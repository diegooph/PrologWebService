package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QtdItensPlacaListagem {
    private String placaVeiculo;
    private int qtdCritica;
    private int qtdAlta;
    private int qtdBaixa;

    public QtdItensPlacaListagem() {

    }

    @NotNull
    public static QtdItensPlacaListagem createDummy() {
        final QtdItensPlacaListagem ordem = new QtdItensPlacaListagem();
        ordem.setPlacaVeiculo("AAA1234");
        ordem.setQtdBaixa(3);
        ordem.setQtdAlta(1);
        ordem.setQtdCritica(7);
        return ordem;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public int getQtdCritica() {
        return qtdCritica;
    }

    public void setQtdCritica(final int qtdCritica) {
        this.qtdCritica = qtdCritica;
    }

    public int getQtdAlta() {
        return qtdAlta;
    }

    public void setQtdAlta(final int qtdAlta) {
        this.qtdAlta = qtdAlta;
    }

    public int getQtdBaixa() {
        return qtdBaixa;
    }

    public void setQtdBaixa(final int qtdBaixa) {
        this.qtdBaixa = qtdBaixa;
    }
}