package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import com.google.common.base.Preconditions;

/**
 * Created on 31/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuEmUso extends Pneu {

    public PneuEmUso() {
        super(PneuTipo.PNEU_EM_USO);
        setStatus(StatusPneu.EM_USO);
    }

    @Override
    public void setStatus(final StatusPneu status) {
        Preconditions.checkArgument(status == StatusPneu.EM_USO);
        super.setStatus(status);
    }
}