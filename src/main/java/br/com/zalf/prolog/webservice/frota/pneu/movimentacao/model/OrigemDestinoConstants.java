package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;

/**
 * Created by Zart on 02/03/17.
 */
public final class OrigemDestinoConstants {

    public static final String VEICULO   = Pneu.EM_USO;
    public static final String ESTOQUE   = Pneu.ESTOQUE;
    public static final String DESCARTE  = Pneu.DESCARTE;
    public static final String ANALISE = Pneu.ANALISE;

    private OrigemDestinoConstants() {
        // You shall not pass!!!
    }
}