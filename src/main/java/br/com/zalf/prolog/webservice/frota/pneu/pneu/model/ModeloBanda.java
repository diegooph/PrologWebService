package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;


import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;

/**
 * Created by Zart on 02/06/2017.
 */
public class ModeloBanda extends Modelo {
    public static final String TIPO_MODELO_BANDA = "MODELO_BANDA";
    private int quantidadeSulcos;
    private Double alturaSulcos;

    public ModeloBanda() {
        setTipo(TIPO_MODELO_BANDA);
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

    @Override
    public String toString() {
        return "ModeloBanda{" +
                "quantidadeSulcos=" + quantidadeSulcos +
                ", alturaSulcos=" + alturaSulcos +
                '}';
    }
}
