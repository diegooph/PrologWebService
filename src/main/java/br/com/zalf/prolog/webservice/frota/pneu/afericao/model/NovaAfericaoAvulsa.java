package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class NovaAfericaoAvulsa extends NovaAfericao {
    private PneuAfericaoAvulsa pneParaAferir;

    public NovaAfericaoAvulsa() {
        super(TipoProcessoColetaAfericao.PNEU_AVULSO);
    }

    public PneuAfericaoAvulsa getPneParaAferir() {
        return pneParaAferir;
    }

    public void setPneParaAferir(final PneuAfericaoAvulsa pneParaAferir) {
        this.pneParaAferir = pneParaAferir;
    }
}