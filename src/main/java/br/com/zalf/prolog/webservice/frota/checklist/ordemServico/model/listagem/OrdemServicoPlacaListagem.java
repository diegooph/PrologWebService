package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.listagem;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrdemServicoPlacaListagem {
    private String placaVeiculo;
    private int qtdCritica;
    private int qtdAlta;
    private int qtdBaixa;

    public OrdemServicoPlacaListagem() {

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