package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

/**
 * Created by Zalf on 22/11/16.
 */
public class DevNfAcumulado extends IndicadorQtdAcumulado {

    public static final String DEV_NF_ACUMULADO = "DEV_NF_ACUMULADO";

    public String getTipo(){
        return DEV_NF_ACUMULADO;
    }

    @Override
    public DevNfAcumulado setTotalOk(int totalOk) {
        super.setTotalOk(totalOk);
        return this;
    }

    @Override
    public DevNfAcumulado setTotalNok(int totalNok) {
        super.setTotalNok(totalNok);
        return this;
    }

    @Override
    public DevNfAcumulado setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DevNfAcumulado setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }

    public void calculaResultado(){
        /*
        ok = hl entregues
        nok = hl devolvidos
        resultado = nok / total
         */
        this.setResultado(getTotal() > 0 ? (double) getTotalNok() / getTotal() : 0);
        super.setBateuMeta(this.getResultado() <= this.getMeta());
    }


}
