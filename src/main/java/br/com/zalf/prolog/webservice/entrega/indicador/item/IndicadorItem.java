package br.com.zalf.prolog.webservice.entrega.indicador.item;

import br.com.zalf.prolog.webservice.entrega.indicador.Indicador;

import java.util.Date;

/**
 * Created by Zalf on 14/09/16.
 */
public abstract class IndicadorItem extends Indicador {

    private Date data;
    private int mapa;

    public IndicadorItem() {super();
    }

    public Date getData() {
        return data;
    }

    public IndicadorItem setData(Date data) {
        this.data = data;
        return this;
    }

    public int getMapa() {
        return mapa;
    }

    public IndicadorItem setMapa(int mapa) {
        this.mapa = mapa;
        return this;
    }

    @Override
    public String toString() {
        return "IndicadorItem{" +
                super.toString() +
                " data=" + data +
                ", mapa=" + mapa +
                '}';
    }
}
