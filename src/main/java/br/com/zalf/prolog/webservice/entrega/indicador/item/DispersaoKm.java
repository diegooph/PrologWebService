package br.com.zalf.prolog.webservice.entrega.indicador.item;

import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class DispersaoKm extends IndicadorItem {

    public static final String DISPERSAO_KM = "DISPERSAO_KM";

    private double kmPlanejado;
    private double kmPercorrido;
    private double resultado;
    private double meta;

    public DispersaoKm() {
        super();
    }

    public String getTipo(){
        return DISPERSAO_KM;
    }

    public double getKmPlanejado() {
        return kmPlanejado;
    }

    public DispersaoKm setKmPlanejado(double kmPlanejado) {
        this.kmPlanejado = kmPlanejado;
        return this;
    }

    public double getKmPercorrido() {
        return kmPercorrido;
    }

    public DispersaoKm setKmPercorrido(double kmPercorrido) {
        this.kmPercorrido = kmPercorrido;
        return this;
    }

    public double getResultado() {
        return resultado;
    }

    public DispersaoKm setResultado(double resultado) {
        this.resultado = resultado;
        return this;
    }

    public double getMeta() {
        return meta;
    }

    public DispersaoKm setMeta(double meta) {
        this.meta = meta;
        return this;
    }

    public void calculaResultado(){
        if(kmPlanejado > 0) {
            this.resultado = ((kmPercorrido - kmPlanejado) / kmPlanejado);
            this.setBateuMeta(this.getResultado() <= this.getMeta());
        }else{
            this.resultado = 0;
            this.setBateuMeta(false);
        }
    }

    @Override
    public DispersaoKm setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    @Override
    public DispersaoKm setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public DispersaoKm setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public String toString() {
        return "DispersaoKm{" +
                super.toString() +
                ", kmPlanejado=" + kmPlanejado +
                ", kmPercorrido=" + kmPercorrido +
                ", resultado=" + resultado +
                '}';
    }
}
