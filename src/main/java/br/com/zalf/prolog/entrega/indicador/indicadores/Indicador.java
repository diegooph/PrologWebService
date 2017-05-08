package br.com.zalf.prolog.entrega.indicador.indicadores;

/**
 * Created by jean on 01/09/16.
 */
public abstract class Indicador {

    private String tipo;
    private boolean bateuMeta;

    public Indicador() {
        tipo = getTipo();
    }

    public abstract String getTipo();

    public boolean isBateuMeta() {
        return bateuMeta;
    }

    public Indicador setBateuMeta(boolean bateuMeta) {
        this.bateuMeta = bateuMeta;
        return this;
    }

    public abstract void calculaResultado();

    @Override
    public String toString() {
        return "Indicador{" +
                "tipo='" + tipo + '\'' +
                ", bateuMeta=" + bateuMeta +
                '}';
    }
}
