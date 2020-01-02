package br.com.zalf.prolog.webservice.frota.pneu._model;

import com.google.common.base.Preconditions;

/**
 * Created on 31/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuDescarte extends Pneu {

    public PneuDescarte() {
        super(PneuTipo.PNEU_DESCARTE);
        setStatus(StatusPneu.DESCARTE);
    }

    @Override
    public void setStatus(final StatusPneu status) {
        Preconditions.checkArgument(status == StatusPneu.DESCARTE);
        super.setStatus(status);
    }
}