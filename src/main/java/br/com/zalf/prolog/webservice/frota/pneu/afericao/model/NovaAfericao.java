package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.util.List;

/**
 * Created by jean on 08/04/16.
 */
public class NovaAfericao {

    private Restricao restricao;
    private Veiculo veiculo;

    /**
     * Os estepes de um {@link Veiculo} não são aferíveis, por isso eles vêm separada em uma lista
     * e não junto dos {@link Veiculo#listPneus} do {@link Veiculo}
     */
    private List<Pneu> estepesVeiculo;

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

    public List<Pneu> getEstepesVeiculo() {
        return estepesVeiculo;
    }

    public void setEstepesVeiculo(List<Pneu> estepesVeiculo) {
        this.estepesVeiculo = estepesVeiculo;
    }

    @Override
    public String toString() {
        return "NovaAfericao{" +
                "restricao=" + restricao +
                ", veiculo=" + veiculo +
                ", estepesVeiculo=" + estepesVeiculo +
                '}';
    }
}