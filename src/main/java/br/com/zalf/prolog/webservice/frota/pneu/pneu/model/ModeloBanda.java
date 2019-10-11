package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;


import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;

/**
 * @deprecated em 2019-10-11 por conta da criação de objetos específicos para o crud de bandas e modelos de pneu.
 * Esse objeto precisa ser refatorado.
 */
@Deprecated
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
