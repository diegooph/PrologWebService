package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;

/**
 * Created by Zart on 02/06/2017.
 */
public class ModeloPneu extends Modelo {
    public static final String TIPO_MODELO_PNEU = "MODELO_PNEU";
    private int quantidadeSulcos;
    private Double alturaSulcos;

    public ModeloPneu() {
        setTipo(TIPO_MODELO_PNEU);
    }

    public int getQuantidadeSulcos() {
        return quantidadeSulcos;
    }

    public void setQuantidadeSulcos(int quantidadeSulcos) {
        this.quantidadeSulcos = quantidadeSulcos;
    }

    public Double getAlturaSulcos() {
        return alturaSulcos;
    }

    public void setAlturaSulcos(Double alturaSulcos) {
        this.alturaSulcos = alturaSulcos;
    }
}