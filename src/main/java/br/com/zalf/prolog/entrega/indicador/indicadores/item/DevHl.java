package br.com.zalf.prolog.entrega.indicador.indicadores.item;

import java.util.Date;

/**
 * Created by jean on 01/09/16.
 */
public class DevHl extends IndicadorQtd {

    public static final String DEVOLUCAO_HL = "DEV_HL";

    public DevHl() {
        super();
    }

    @Override
    public DevHl setData(Date data) {
        super.setData(data);
        return this;
    }

    @Override
    public DevHl setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DevHl setBateuMeta(boolean bateuMeta) {
        super.setBateuMeta(bateuMeta);
        return this;
    }

    @Override
    public DevHl setOk(double ok){
        super.setOk(ok);
        return this;
    }

    @Override
    public DevHl setNok(double nok){
        super.setNok(nok);
        return this;
    }

    @Override
    public DevHl setMapa(int mapa) {
        super.setMapa(mapa);
        return this;
    }

    public String getTipo(){
        return DEVOLUCAO_HL;
    }

    public void calculaResultado(){
        /*
        ok = hl entregues
        nok = hl devolvidos
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
        return "DevHl{" +
                super.toString() +
                '}';
    }
}
