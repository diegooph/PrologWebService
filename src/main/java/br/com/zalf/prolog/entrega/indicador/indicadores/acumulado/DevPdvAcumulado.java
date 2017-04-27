package br.com.zalf.prolog.entrega.indicador.indicadores.acumulado;

/**
 * Created by jean on 03/09/16.
 */
public class DevPdvAcumulado extends IndicadorQtdAcumulado {


    public static final String DEV_PDV_ACUMULADO = "DEV_PDV_ACUMULADO";

    public String getTipo(){
        return DEV_PDV_ACUMULADO;
    }

    @Override
    public DevPdvAcumulado setTotalOk(int totalOk) {
        super.setTotalOk(totalOk);
        return this;
    }

    @Override
    public DevPdvAcumulado setTotalNok(int totalNok) {
        super.setTotalNok(totalNok);
        return this;
    }

    @Override
    public DevPdvAcumulado setMeta(double meta) {
        super.setMeta(meta);
        return this;
    }

    @Override
    public DevPdvAcumulado setResultado(double resultado) {
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
