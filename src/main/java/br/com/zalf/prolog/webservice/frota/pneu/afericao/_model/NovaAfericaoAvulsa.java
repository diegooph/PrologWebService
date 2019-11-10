package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class NovaAfericaoAvulsa extends NovaAfericao {
    private PneuAfericaoAvulsa pneuParaAferir;

    public NovaAfericaoAvulsa() {
        super(TipoProcessoColetaAfericao.PNEU_AVULSO);
    }

    public PneuAfericaoAvulsa getPneuParaAferir() {
        return pneuParaAferir;
    }

    public void setPneuParaAferir(final PneuAfericaoAvulsa pneParaAferir) {
        this.pneuParaAferir = pneParaAferir;
    }
}