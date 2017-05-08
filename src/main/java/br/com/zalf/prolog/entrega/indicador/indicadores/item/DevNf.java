package br.com.zalf.prolog.entrega.indicador.indicadores.item;

import java.util.Date;

/**
 * Created by Zalf on 22/11/16.
 */
public class DevNf extends IndicadorQtd{

    public static final String DEVOLUCAO_NF = "DEV_NF";

    public DevNf() {
        super();
    }

    @Override
    public DevNf setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public DevNf setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DevNf setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public DevNf setOk(double ok){
        super.setOk(ok);
        return this;
    }

    @Override
    public DevNf setNok(double nok){
        super.setNok(nok);
        return this;
    }

    @Override
    public DevNf setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    public String getTipo(){
        return DEVOLUCAO_NF;
    }

    public void calculaResultado(){
        /*
        ok = pdvs entregues
        nok = pdvs devolvidos
        resultado = nok / total
         */
        this.setResultado(getTotal() > 0 ? getNok() / getTotal() : 0);
        this.setBateuMeta(this.getResultado() <= this.getMeta());
    }

    private double getTotal(){
        return getOk() + getNok();
    }

    @Override
    public String toString() {
        return "DevNf{" +
                super.toString() +
                '}';
    }

}
