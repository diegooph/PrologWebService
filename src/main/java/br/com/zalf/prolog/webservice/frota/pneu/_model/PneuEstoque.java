package br.com.zalf.prolog.webservice.frota.pneu._model;

import com.google.common.base.Preconditions;

/**
 * Created on 31/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuEstoque extends Pneu {

    public PneuEstoque() {
        super(PneuTipo.PNEU_ESTOQUE);
        setStatus(StatusPneu.ESTOQUE);
    }

    @Override
    public void setStatus(final StatusPneu status) {
        Preconditions.checkArgument(status == StatusPneu.ESTOQUE);
        super.setStatus(status);
    }
}