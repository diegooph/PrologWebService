package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoEnum;

/**
 * Created by Zart on 02/03/17.
 */
public final class DestinoEstoque extends Destino {

    public DestinoEstoque() {
        super(OrigemDestinoEnum.ESTOQUE);
    }
}
