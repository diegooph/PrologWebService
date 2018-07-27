package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AfericaoAvulsa extends Afericao {

    private PneuAfericaoAvulsa pneuAferido;

    public AfericaoAvulsa() {
        super(TipoProcessoColetaAfericao.PNEU_AVULSO);
    }

    public PneuAfericaoAvulsa getPneuAferido() {
        return pneuAferido;
    }

    public void setPneuAferido(final PneuAfericaoAvulsa pneuAferido) {
        this.pneuAferido = pneuAferido;
    }
}