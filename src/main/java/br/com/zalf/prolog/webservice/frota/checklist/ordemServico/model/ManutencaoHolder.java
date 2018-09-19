package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

/**
 * Created by jean on 09/03/16.
 */
public class ManutencaoHolder {

    private Veiculo veiculo;
    private int qtdCritica;
    private int qtdAlta;
    private int qtdBaixa;

    public ManutencaoHolder() {
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public int getQtdCritica() {
        return qtdCritica;
    }

    public void setQtdCritica(int qtdCritica) {
        this.qtdCritica = qtdCritica;
    }

    public int getQtdAlta() {
        return qtdAlta;
    }

    public void setQtdAlta(int qtdAlta) {
        this.qtdAlta = qtdAlta;
    }

    public int getQtdBaixa() {
        return qtdBaixa;
    }

    public void setQtdBaixa(int qtdBaixa) {
        this.qtdBaixa = qtdBaixa;
    }

    @Override
    public String toString() {
        return "ManutencaoHolder{" +
                "veiculo=" + veiculo +
                ", qtdCritica=" + qtdCritica +
                ", qtdAlta=" + qtdAlta +
                ", qtdBaixa=" + qtdBaixa +
                '}';
    }
}
