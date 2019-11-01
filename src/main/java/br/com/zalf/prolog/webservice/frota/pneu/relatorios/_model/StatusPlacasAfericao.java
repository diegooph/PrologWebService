package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

/**
 * Created on 1/26/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class StatusPlacasAfericao {
    private final int qtdPlacasAfericaoVencida;
    private final int qtdPlacasAfericaoNoPrazo;

    public StatusPlacasAfericao(final int qtdPlacasAfericaoVencida, final int qtdPlacasAfericaoNoPrazo) {
        this.qtdPlacasAfericaoVencida = qtdPlacasAfericaoVencida;
        this.qtdPlacasAfericaoNoPrazo = qtdPlacasAfericaoNoPrazo;
    }

    public int getQtdPlacasAfericaoVencida() {
        return qtdPlacasAfericaoVencida;
    }

    public int getQtdPlacasAfericaoNoPrazo() {
        return qtdPlacasAfericaoNoPrazo;
    }
}