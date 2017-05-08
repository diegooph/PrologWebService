package br.com.zalf.prolog.entrega.indicador.indicadores.item;

import java.util.Date;

/**
 * Created by jean on 31/08/16.
 */
public class DevPdv extends IndicadorQtd {

    public static final String DEVOLUCAO_PDV = "DEV_PDV";

    public DevPdv() {
        super();
    }

    @Override
    public DevPdv setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public DevPdv setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DevPdv setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public DevPdv setOk(double ok){
        super.setOk(ok);
        return this;
    }

    @Override
    public DevPdv setNok(double nok){
        super.setNok(nok);
        return this;
    }

    @Override
    public DevPdv setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    public String getTipo(){
        return DEVOLUCAO_PDV;
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
        return "DevPdv{" +
                super.toString() +
                '}';
    }
}
