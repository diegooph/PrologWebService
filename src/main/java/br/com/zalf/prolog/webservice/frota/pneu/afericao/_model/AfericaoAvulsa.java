package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AfericaoAvulsa extends Afericao {

    private Pneu pneuAferido;

    public AfericaoAvulsa() {
        super(TipoProcessoColetaAfericao.PNEU_AVULSO);
    }

    public Pneu getPneuAferido() {
        return pneuAferido;
    }

    public void setPneuAferido(final Pneu pneuAferido) {
        this.pneuAferido = pneuAferido;
    }

    @NotNull
    @Override
    public List<Pneu> getPneusAferidos() {
        final List<Pneu> pneusAferidos = new ArrayList<>();
        pneusAferidos.add(pneuAferido);
        return pneusAferidos;
    }
}