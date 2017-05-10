package br.com.zalf.prolog.webservice.entrega.indicador.item;

import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class Tracking extends IndicadorQtd{

    public static final String TRACKING = "TRACKING";

    public Tracking() {
        super();
    }

    @Override
    public Tracking setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public Tracking setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public Tracking setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public Tracking setOk(double ok){
        super.setOk(ok);
        return this;
    }

    @Override
    public Tracking setNok(double nok){
        super.setNok(nok);
        return this;
    }

    @Override
    public Tracking setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }
    

    public String getTipo(){
        return TRACKING;
    }

    public void calculaResultado(){
        /*
        ok = apontamentos OK
        nok = apontamentos NOK
        resultado = nok / total
         */
        this.setResultado(getTotal() > 0 ? getOk() / getTotal() : 0);
        this.setBateuMeta(this.getResultado() >= this.getMeta());
    }

    private double getTotal(){
        return getOk() + getNok();
    }

    @Override
    public String toString() {
        return "Tracking{" +
                super.toString() +
                '}';
    }

}
