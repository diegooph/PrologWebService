package br.com.zalf.prolog.webservice.frota.pneu.relatorios.model;

/**
 * Created on 1/26/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class StatusPlacasAfericao {
    private int qtdPlacasAfericaoVencida;
    private int qtdPlacasAfericaoNoPrazo;

    public StatusPlacasAfericao() {

    }

    public StatusPlacasAfericao(int qtdPlacasAfericaoVencida, int qtdPlacasAfericaoNoPrazo) {
        this.qtdPlacasAfericaoVencida = qtdPlacasAfericaoVencida;
        this.qtdPlacasAfericaoNoPrazo = qtdPlacasAfericaoNoPrazo;
    }

    public int getQtdPlacasAfericaoVencida() {
        return qtdPlacasAfericaoVencida;
    }

    public void setQtdPlacasAfericaoVencida(int qtdPlacasAfericaoVencida) {
        this.qtdPlacasAfericaoVencida = qtdPlacasAfericaoVencida;
    }

    public int getQtdPlacasAfericaoNoPrazo() {
        return qtdPlacasAfericaoNoPrazo;
    }

    public void setQtdPlacasAfericaoNoPrazo(int qtdPlacasAfericaoNoPrazo) {
        this.qtdPlacasAfericaoNoPrazo = qtdPlacasAfericaoNoPrazo;
    }
}