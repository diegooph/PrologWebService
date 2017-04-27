package br.com.zalf.prolog.frota.pneu.afericao;

import br.com.zalf.prolog.commons.veiculo.Veiculo;
import br.com.zalf.prolog.frota.pneu.Restricao;

/**
 * Created by jean on 08/04/16.
 */
public class NovaAfericao {

    private Restricao restricao;
    private Veiculo veiculo;

    public NovaAfericao() {
    }

    public Restricao getRestricao() {
        return restricao;
    }

    public void setRestricao(Restricao restricao) {
        this.restricao = restricao;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    @Override
    public String toString() {
        return "NovaAfericao{" +
                "restricao=" + restricao +
                ", veiculo=" + veiculo +
                '}';
    }
}
