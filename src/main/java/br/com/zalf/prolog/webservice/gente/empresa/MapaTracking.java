package br.com.zalf.prolog.webservice.gente.empresa;

/**
 * Created by jean on 05/08/16.
 * Objeto usado na tela do Android onde são verificados os dias que tem upload realizado
 */
public class MapaTracking {

    private Integer mapa;
    private Integer tracking;
    private String placa;

    public MapaTracking() {
    }

    public Integer getMapa() {
        return mapa;
    }

    public void setMapa(Integer mapa) {
        this.mapa = mapa;
    }

    public Integer getTracking() {
        return tracking;
    }

    public void setTracking(Integer tracking) {
        this.tracking = tracking;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    @Override
    public String toString() {
        return "MapaTracking{" +
                "mapa=" + mapa +
                ", tracking=" + tracking +
                ", placa='" + placa + '\'' +
                '}';
    }
}

