package br.com.zalf.prolog.webservice.entrega.indicador.acumulado;

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
        double valor = (double) getTotalNok() / getTotal();
        this.setResultado(getTotal() > 0 ? Math.floor(((double) getTotalNok() / getTotal())*10000)/10000 : 0);
        super.setBateuMeta(this.getResultado() <= this.getMeta());
        System.out.println(super.isBateuMeta());
    }

}
