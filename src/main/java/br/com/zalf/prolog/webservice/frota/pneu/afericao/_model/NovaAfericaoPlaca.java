package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.util.List;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class NovaAfericaoPlaca extends NovaAfericao {
    private Veiculo veiculo;

    /**
     * Os estepes de um {@link Veiculo} não são aferíveis, por isso eles vêm separada em uma lista
     * e não junto dos {@link Veiculo#listPneus} do {@link Veiculo}
     */
    private List<Pneu> estepesVeiculo;

    /**
     * A afreição de estepes é uma opção de cada {@link Unidade}. Cada Unidade define quais {@link TipoVeiculo}
     * terão seus estepes aferíveis ou não. Assim este atributo serve para dizer se devemos aferir
     * os {@code estepesVeiculo} do {@code veiculo} em questão.
     */
    private boolean deveAferirEstepes;

    public NovaAfericaoPlaca() {
        super(TipoProcessoColetaAfericao.PLACA);
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

    public boolean isDeveAferirEstepes() {
        return deveAferirEstepes;
    }

    public void setDeveAferirEstepes(final boolean deveAferirEstepes) {
        this.deveAferirEstepes = deveAferirEstepes;
    }
}