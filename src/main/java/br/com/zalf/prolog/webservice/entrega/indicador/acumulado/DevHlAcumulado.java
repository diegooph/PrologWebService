package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public class DevHlAcumulado extends IndicadorQtdAcumulado {

    public static final String DEV_HL_ACUMULADO = "DEV_HL_ACUMULADO";

    public String getTipo(){
        return DEV_HL_ACUMULADO;
    }

    @Override
    public DevHlAcumulado setTotalOk(int totalOk) {
        super.setTotalOk(totalOk);
        return this;
    }

    @Override
    public DevHlAcumulado setTotalNok(int totalNok) {
        super.setTotalNok(totalNok);
        return this;
    }

    @Override
    public DevHlAcumulado setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DevHlAcumulado setResultado(double resultado) {
        super.setResultado(resultado);
        return this;
    }

    public void calculaResultado(){
        /*
        ok = hl entregues
        nok = hl devolvidos
        resultado = nok / total
         */
        this.setResultado(getTotal() > 0 ? Math.floor(((double) getTotalNok() / getTotal())*10000)/10000 : 0);
        super.setBateuMeta(this.getResultado() <= this.getMeta());
    }


}
