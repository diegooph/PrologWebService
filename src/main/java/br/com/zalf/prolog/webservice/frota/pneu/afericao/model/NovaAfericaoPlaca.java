package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.PneuComum;
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
    private List<PneuComum> estepesVeiculo;

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

    public List<PneuComum> getEstepesVeiculo() {
        return estepesVeiculo;
    }

    public void setEstepesVeiculo(List<PneuComum> estepesVeiculo) {
        this.estepesVeiculo = estepesVeiculo;
    }

    public boolean isDeveAferirEstepes() {
        return deveAferirEstepes;
    }

    public void setDeveAferirEstepes(final boolean deveAferirEstepes) {
        this.deveAferirEstepes = deveAferirEstepes;
    }
}