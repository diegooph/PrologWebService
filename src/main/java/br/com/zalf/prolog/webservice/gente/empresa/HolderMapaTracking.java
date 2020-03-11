package br.com.zalf.prolog.webservice.gente.empresa;

import java.sql.Date;
import java.util.List;

/**
 * Created by jean on 08/08/16.
 */
public class HolderMapaTracking {

    private Date data;
    private List<MapaTracking> mapas;

    public HolderMapaTracking() {
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<MapaTracking> getMapas() {
        return mapas;
    }

    public void setMapas(List<MapaTracking> mapas) {
        this.mapas = mapas;
    }

    @Override
    public String toString() {
        return "HolderMapaTracking{" +
                "data=" + data +
                ", mapas=" + mapas +
                '}';
    }
}
