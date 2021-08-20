package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

/**
 * Created on 1/26/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class StatusPlacasAfericao {
    private final int qtdPlacasAfericaoVencida;
    private final int qtdPlacasAfericaoNoPrazo;
    private final int qtdPlacasNuncaAferidas;

    public StatusPlacasAfericao(final int qtdPlacasAfericaoVencida, final int qtdPlacasAfericaoNoPrazo) {
        this.qtdPlacasAfericaoVencida = qtdPlacasAfericaoVencida;
        this.qtdPlacasAfericaoNoPrazo = qtdPlacasAfericaoNoPrazo;
        this.qtdPlacasNuncaAferidas = 0;
    }

    public StatusPlacasAfericao(final int qtdPlacasAfericaoVencida, final int qtdPlacasAfericaoNoPrazo,final int qtdPlacasNuncaAferidas) {
        this.qtdPlacasAfericaoVencida = qtdPlacasAfericaoVencida;
        this.qtdPlacasNuncaAferidas = qtdPlacasNuncaAferidas;
        this.qtdPlacasAfericaoNoPrazo = qtdPlacasAfericaoNoPrazo;
    }
    public int getQtdPlacasAfericaoVencida() {
        return qtdPlacasAfericaoVencida;
    }

    public int getQtdPlacasAfericaoNoPrazo() {
        return qtdPlacasAfericaoNoPrazo;
    }

    public int getQtdPlacasNuncaAferidas() {
        return qtdPlacasNuncaAferidas;
    }
}