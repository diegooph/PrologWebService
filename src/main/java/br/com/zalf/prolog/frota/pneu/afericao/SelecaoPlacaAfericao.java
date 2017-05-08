package br.com.zalf.prolog.frota.pneu.afericao;

import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public class SelecaoPlacaAfericao {

    List<PlacaModeloHolder> placas;
    private int meta;

    public SelecaoPlacaAfericao() {
    }

    public List<PlacaModeloHolder> getPlacas() {
        return placas;
    }

    public void setPlacas(List<PlacaModeloHolder> placas) {
        this.placas = placas;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "SelecaoPlacaAfericao{" +
                "placas=" + placas +
                ", meta=" + meta +
                '}';
    }
}
